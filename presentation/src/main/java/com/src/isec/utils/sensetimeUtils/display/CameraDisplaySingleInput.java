package com.src.isec.utils.sensetimeUtils.display;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.sensetime.stmobile.STBeautifyNative;
import com.sensetime.stmobile.STBeautyParamsType;
import com.sensetime.stmobile.STCommon;
import com.sensetime.stmobile.STFilterParamsType;
import com.sensetime.stmobile.STHumanActionParamsType;
import com.sensetime.stmobile.STMobileFaceAttributeNative;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STMobileObjectTrackNative;
import com.sensetime.stmobile.STMobileStickerNative;
import com.sensetime.stmobile.STMobileStreamFilterNative;
import com.sensetime.stmobile.model.STFaceAttribute;
import com.sensetime.stmobile.model.STHumanAction;
import com.sensetime.stmobile.model.STMobile106;
import com.sensetime.stmobile.model.STRect;
import com.src.isec.mvp.view.implement.activity.PublisherActivity;
import com.src.isec.utils.sensetimeUtils.Accelerometer;
import com.src.isec.utils.sensetimeUtils.CameraProxy;
import com.src.isec.utils.sensetimeUtils.FileUtils;
import com.src.isec.utils.sensetimeUtils.encoder.MediaVideoEncoder;
import com.src.isec.utils.sensetimeUtils.glutils.GlUtil;
import com.src.isec.utils.sensetimeUtils.glutils.OpenGLUtils;
import com.src.isec.utils.sensetimeUtils.glutils.STUtils;
import com.src.isec.utils.sensetimeUtils.glutils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * CameraDisplaySingleInput is used for camera preview
 */

/**
 * 渲染结果显示的Render, 用户最终看到的结果在这个类中得到并显示.
 * 请重点关注Camera.PreviewCallback的onPreviewFrame/onSurfaceCreated,onSurfaceChanged,onDrawFrame
 * 四个接口, 基本上所有的处理逻辑都是围绕这四个接口展开
 */
public class CameraDisplaySingleInput implements Renderer {

    private String TAG = "CameraDisplaySingleInput";
    private boolean DEBUG = false;
    /**
     * SurfaceTexure texture id
     */
    protected int mTextureId = OpenGLUtils.NO_TEXTURE;

    private int mImageWidth;
    private int mImageHeight;
    private GLSurfaceView mGlSurfaceView;
    private ChangePreviewSizeListener mListener;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private Context mContext;

    public CameraProxy mCameraProxy;
    private SurfaceTexture mSurfaceTexture;
    private String mCurrentSticker;
    private String mCurrentFilterStyle;
    private float mCurrentFilterStrength = 0.65f;//阈值为[0,1]
    private float mFilterStrength = 0.65f;
    private String mFilterStyle;

    private int mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private STGLRender mGLRender;
    private STMobileStickerNative mStStickerNative = new STMobileStickerNative();
    private STBeautifyNative mStBeautifyNative = new STBeautifyNative();
    private STMobileHumanActionNative mSTHumanActionNative = new STMobileHumanActionNative();
    private STHumanAction mHumanActionBeautyOutput = new STHumanAction();
    private STMobileStreamFilterNative mSTMobileStreamFilterNative = new STMobileStreamFilterNative();
    private STMobileFaceAttributeNative mSTFaceAttributeNative = new STMobileFaceAttributeNative();
    private STMobileObjectTrackNative mSTMobileObjectTrackNative = new STMobileObjectTrackNative();

    private ByteBuffer mRGBABuffer;
    private int[] mBeautifyTextureId;
    private int[] mTextureOutId;
    private int[] mFilterTextureOutId;
    private boolean mCameraChanging = false;
    private int mCurrentPreview = 0;
    private ArrayList<String> mSupportedPreviewSizes;
    private boolean mSetPreViewSizeSucceed = false;
    private boolean mIsChangingPreviewSize = false;

    private long mStartTime;
    private boolean mShowOriginal = false;
    private boolean mNeedBeautify = false;
    private boolean mNeedSticker = false;
    private boolean mNeedFilter = false;
    private boolean mNeedSave = false;
    private boolean mNeedObject = false;
    private FloatBuffer mTextureBuffer;
    private float[] mBeautifyParams = {0.36f, 0.74f, 0.02f, 0.13f, 0.11f, 0.1f, 0f, 0f};

    public static int[] beautyTypes = {
            STBeautyParamsType.ST_BEAUTIFY_REDDEN_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_SMOOTH_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_WHITEN_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_ENLARGE_EYE_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_SHRINK_FACE_RATIO,
            STBeautyParamsType.ST_BEAUTIFY_SHRINK_JAW_RATIO,

            STBeautyParamsType.ST_BEAUTIFY_CONSTRACT_STRENGTH,
            STBeautyParamsType.ST_BEAUTIFY_SATURATION_STRENGTH,
    };
    private Handler mHandler;
    private String mFaceAttribute;
    private boolean mIsPaused = false;
    private long mDetectConfig = 0;
    private boolean mIsCreateHumanActionHandleSucceeded = false;
    private Object mHumanActionHandleLock = new Object();

    private boolean mNeedShowRect = true;
    private int mScreenIndexRectWidth = 0;

    private Rect mTargetRect = new Rect();
    private Rect mIndexRect = new Rect();
    private boolean mNeedSetObjectTarget = false;
    private boolean mIsObjectTracking = false;

    private int mHumanActionCreateConfig = STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO;
    private long mRotateCost = 0;
    private long mObjectCost = 0;
    private long mFaceAttributeCost = 0;
    private int mFrameCount = 0;

    //for test fps
    private float mFps;
    private int mCount = 0;
    private long mCurrentTime = 0;
    private boolean mIsFirstCount = true;
    private int mFrameCost = 0;

    private MediaVideoEncoder mVideoEncoder;
    private final float[] mStMatrix = new float[16];
    private int[] mVideoEncoderTexture;
    private boolean mNeedResetEglContext = false;

    private static final int MESSAGE_ADD_SUB_MODEL = 1001;
    private static final int MESSAGE_REMOVE_SUB_MODEL = 1002;
    private static final int MESSAGE_NEED_CHANGE_STICKER = 1003;

    private HandlerThread mSubModelsManagerThread;
    private Handler mSubModelsManagerHandler;

    private HandlerThread mChangeStickerManagerThread;
    private Handler mChangeStickerManagerHandler;

    private long mHandAction = 0;
    private long mBodyAction = 0;
    private boolean[] mFaceExpressionResult;

    public interface ChangePreviewSizeListener {
        void onChangePreviewSize(int previewW, int previewH);
    }

    public CameraDisplaySingleInput(Context context, ChangePreviewSizeListener listener, GLSurfaceView glSurfaceView) {
        mCameraProxy = new CameraProxy(context);
        mGlSurfaceView = glSurfaceView;
        mListener = listener;
        mContext = context;
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(this);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

        mTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        mGLRender = new STGLRender();

        //是否为debug模式
//        DEBUG = CameraActivity.DEBUG;

        //初始化非OpengGL相关的句柄，包括人脸检测及人脸属性
        initHumanAction(); //因为人脸模型加载较慢，建议异步调用
        initObjectTrack();

//        if(DEBUG){
            initFaceAttribute();
//        }

        initHandlerManager();
    }

    private void initHandlerManager(){
        mSubModelsManagerThread = new HandlerThread("SubModelManagerThread");
        mSubModelsManagerThread.start();
        mSubModelsManagerHandler = new Handler(mSubModelsManagerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(!mIsPaused && !mCameraChanging && mIsCreateHumanActionHandleSucceeded){
                    switch (msg.what){
                        case MESSAGE_ADD_SUB_MODEL:
                            String modelName = (String) msg.obj;
                            if(modelName != null){
                                addSubModel(modelName);
                            }
                            break;

                        case MESSAGE_REMOVE_SUB_MODEL:
                            int config = (int) msg.obj;
                            if(config != 0){
                                removeSubModel(config);
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        };

        mChangeStickerManagerThread = new HandlerThread("ChangeStickerManagerThread");
        mChangeStickerManagerThread.start();
        mChangeStickerManagerHandler = new Handler(mChangeStickerManagerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(!mIsPaused && !mCameraChanging){
                    switch (msg.what){
                        case MESSAGE_NEED_CHANGE_STICKER:
                            String sticker = (String) msg.obj;
                            mCurrentSticker = sticker;

                            mStStickerNative.changeSticker(mCurrentSticker);
                            setHumanActionDetectConfig(mNeedBeautify, mStStickerNative.getTriggerAction());
                            break;

                        default:
                            break;
                    }
                }
            }
        };
    }

    private void addSubModel(final String modelName){
        synchronized (mHumanActionHandleLock) {
            int result = mSTHumanActionNative.addSubModelFromAssetFile(modelName, mContext.getAssets());
//            LogUtils.i(TAG, "add sub model result: %d", result);

            if(result == 0){
                if(modelName.equals(FileUtils.MODEL_NAME_BODY_FOURTEEN)){
                    mDetectConfig |= STMobileHumanActionNative.ST_MOBILE_BODY_KEYPOINTS;
                    mSTHumanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_BODY_LIMIT, 3.0f);
                }else if(modelName.equals(FileUtils.MODEL_NAME_FACE_EXTRA)){
                    mDetectConfig |= STMobileHumanActionNative.ST_MOBILE_DETECT_EXTRA_FACE_POINTS;
                }else if(modelName.equals(FileUtils.MODEL_NAME_EYEBALL_CONTOUR)){
                    mDetectConfig |= STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CONTOUR |
                            STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CENTER;
                }else if(modelName.equals(FileUtils.MODEL_NAME_HAND)){
                    mDetectConfig |= STMobileHumanActionNative.ST_MOBILE_HAND_DETECT_FULL;
                }
            }
        }
    }

    private void removeSubModel(final int config){
        synchronized (mHumanActionHandleLock) {
            int result = mSTHumanActionNative.removeSubModelByConfig(config);
//            LogUtils.i(TAG, "remove sub model result: %d", result);

            if(config == STMobileHumanActionNative.ST_MOBILE_ENABLE_BODY_KEYPOINTS){
                mDetectConfig &= ~STMobileHumanActionNative.ST_MOBILE_BODY_KEYPOINTS;
            }else if(config == STMobileHumanActionNative.ST_MOBILE_ENABLE_FACE_EXTRA_DETECT){
                mDetectConfig &= ~STMobileHumanActionNative.ST_MOBILE_DETECT_EXTRA_FACE_POINTS;
            }else if(config == STMobileHumanActionNative.ST_MOBILE_ENABLE_EYEBALL_CONTOUR_DETECT){
                mDetectConfig &= ~(STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CONTOUR |
                        STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CENTER);
            }else if(config == STMobileHumanActionNative.ST_MOBILE_ENABLE_HAND_DETECT){
                mDetectConfig &= ~STMobileHumanActionNative.ST_MOBILE_HAND_DETECT_FULL;
            }
        }
    }

    public void enableBeautify(boolean needBeautify) {
        mNeedBeautify = needBeautify;
        setHumanActionDetectConfig(mNeedBeautify, mStStickerNative.getTriggerAction());
        mNeedResetEglContext = true;
    }

    public void enableSticker(boolean needSticker){
        mNeedSticker = needSticker;
        //reset humanAction config
        if(!needSticker){
            setHumanActionDetectConfig(mNeedBeautify, mStStickerNative.getTriggerAction());
        }

        mNeedResetEglContext = true;
    }

    public void enableFilter(boolean needFilter){
        mNeedFilter = needFilter;
        mNeedResetEglContext = true;
    }

    public String getFaceAttributeString() {
        return mFaceAttribute;
    }

    public boolean getSupportPreviewsize(int size) {
        if(size == 0 && mSupportedPreviewSizes.contains("640x480")){
            return true;
        }else if(size == 1 && mSupportedPreviewSizes.contains("1280x720")){
            return true;
        }else{
            return false;
        }
    }

    public void setSaveImage() {
        mNeedSave = true;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 工作在opengl线程, 当前Renderer关联的view创建的时候调用
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        LogUtils.i(TAG, "onSurfaceCreated");
        if (mIsPaused == true) {
            return ;
        }
        GLES20.glEnable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);

        while (!mCameraProxy.isCameraOpen())
        {
            if(mCameraProxy.cameraOpenFailed()){
                return;
            }
            try {
                Thread.sleep(10,0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (mCameraProxy.getCamera() != null) {
            setUpCamera();
        }

        //初始化GL相关的句柄，包括美颜，贴纸，滤镜
        initBeauty();
        initSticker();
        initFilter();
    }

    private void initFaceAttribute() {
        int result = mSTFaceAttributeNative.createInstanceFromAssetFile(FileUtils.MODEL_NAME_FACE_ATTRIBUTE, mContext.getAssets());
//        LogUtils.i(TAG, "the result for createInstance for faceAttribute is %d", result);
    }

    private void initHumanAction() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mHumanActionHandleLock) {
                    //从asset资源文件夹读取model到内存，再使用底层st_mobile_human_action_create_from_buffer接口创建handle
                    int result = mSTHumanActionNative.createInstanceFromAssetFile(FileUtils.getActionModelName(), mHumanActionCreateConfig, mContext.getAssets());
//                    LogUtils.i(TAG, "the result for createInstance for human_action is %d", result);

                    if (result == 0) {
                        mIsCreateHumanActionHandleSucceeded = true;
                        mSTHumanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_BACKGROUND_BLUR_STRENGTH, 0.35f);

                        //for test face morph
                        result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_FACE_EXTRA, mContext.getAssets());
//                        LogUtils.i(TAG, "add face extra model result: %d", result);

                        //for test avatar
                        result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_EYEBALL_CONTOUR, mContext.getAssets());
//                        LogUtils.i(TAG, "add eyeball contour model result: %d", result);
                    }
                }
            }
        }).start();
    }

    private void initSticker() {
        int result = mStStickerNative.createInstance(mContext, null);

        if(mNeedSticker){
            mStStickerNative.changeSticker(mCurrentSticker);
        }

        //从sd卡加载Avatar模型
        //mStStickerNative.loadAvatarModel(FileUtils.getAvatarCoreModelPath(mContext));

        //从资源文件加载Avatar模型
        mStStickerNative.loadAvatarModelFromAssetFile(FileUtils.MODEL_NAME_AVATAR_CORE, mContext.getAssets());

        setHumanActionDetectConfig(mNeedBeautify, mStStickerNative.getTriggerAction());
//        LogUtils.i(TAG, "the result for createInstance for human_action is %d", result);
    }

    private void initBeauty() {
        // 初始化beautify,preview的宽高
        int result = mStBeautifyNative.createInstance();
//        LogUtils.i(TAG, "the result is for initBeautify " + result);
        if (result == 0) {
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_REDDEN_STRENGTH, mBeautifyParams[0]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_SMOOTH_STRENGTH, mBeautifyParams[1]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_WHITEN_STRENGTH, mBeautifyParams[2]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_ENLARGE_EYE_RATIO, mBeautifyParams[3]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_SHRINK_FACE_RATIO, mBeautifyParams[4]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_SHRINK_JAW_RATIO, mBeautifyParams[5]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_CONSTRACT_STRENGTH, mBeautifyParams[6]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_SATURATION_STRENGTH, mBeautifyParams[7]);
        }
    }

    /**
     * human action detect的配置选项,根据Sticker的TriggerAction和是否需要美颜配置
     *
     * @param needFaceDetect  是否需要开启face detect
     * @param config  sticker的TriggerAction
     */
    private void setHumanActionDetectConfig(boolean needFaceDetect, long config){
        if(!mNeedSticker || mCurrentSticker == null){
            config = 0;
        }

        if(needFaceDetect){
            mDetectConfig = config |STMobileHumanActionNative.ST_MOBILE_FACE_DETECT;
        }else{
            mDetectConfig = config;
        }
    }

    private void initFilter(){
        mSTMobileStreamFilterNative.createInstance();

        mSTMobileStreamFilterNative.setStyle(mCurrentFilterStyle);

        mCurrentFilterStrength = mFilterStrength;
        mSTMobileStreamFilterNative.setParam(STFilterParamsType.ST_FILTER_STRENGTH, mCurrentFilterStrength);
    }

    private void initObjectTrack(){
        int result = mSTMobileObjectTrackNative.createInstance();
    }

    private void faceAttributeDetect(byte[] data, STHumanAction humanAction){
        if (humanAction != null && data != null && data.length == mImageHeight * mImageWidth * 4) {
            STMobile106[] arrayFaces = null;
            arrayFaces = humanAction.getMobileFaces();

            if (arrayFaces != null && arrayFaces.length != 0) { // face attribute
                STFaceAttribute[] arrayFaceAttribute = new STFaceAttribute[arrayFaces.length];
                long attributeCostTime = System.currentTimeMillis();
                int result = mSTFaceAttributeNative.detect(data, STCommon.ST_PIX_FMT_RGBA8888, mImageWidth, mImageHeight, arrayFaces, arrayFaceAttribute);
//                LogUtils.i(TAG, "attribute cost time: %d", System.currentTimeMillis() - attributeCostTime);
                mFaceAttributeCost = System.currentTimeMillis() - attributeCostTime;
                if (result == 0) {
                    if (arrayFaceAttribute[0].attribute_count > 0) {
                        mFaceAttribute = STFaceAttribute.getFaceAttributeString(arrayFaceAttribute[0]);
                    } else {
                        mFaceAttribute = "null";
                    }
                }
            } else {
                mFaceAttribute = null;
                mFaceAttributeCost = 0;
            }
        }
    }

    public void setBeautyParam(int index, float value) {
        if(mBeautifyParams[index] != value){
            mStBeautifyNative.setParam(beautyTypes[index], value);
            mBeautifyParams[index] = value;
        }
    }

    public float[] getBeautyParams(){
        float[] values = new float[6];
        for(int i = 0; i< mBeautifyParams.length; i++){
            values[i] = mBeautifyParams[i];
        }

        return values;
    }

    public void setShowOriginal(boolean isShow)
    {
        mShowOriginal = isShow;
    }

    /**
     * 工作在opengl线程, 当前Renderer关联的view尺寸改变的时候调用
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        LogUtils.i(TAG, "onSurfaceChanged");
        if (mIsPaused == true) {
            return ;
        }
        adjustViewPort(width, height);

        mGLRender.init(mImageWidth, mImageHeight);
        mStartTime = System.currentTimeMillis();
    }

    /**
     * 根据显示区域大小调整一些参数信息
     *
     * @param width
     * @param height
     */
    private void adjustViewPort(int width, int height) {
        mSurfaceHeight = height;
        mSurfaceWidth = width;
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        mGLRender.calculateVertexBuffer(mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);
    }

    /**
     * 工作在opengl线程, 具体渲染的工作函数
     *
     * @param gl
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onDrawFrame(GL10 gl) {
        // during switch camera
        if (mCameraChanging) {
            return ;
        }

        if (mCameraProxy.getCamera() == null) {
            return;
        }

//        LogUtils.i(TAG, "onDrawFrame");
        if (mRGBABuffer == null) {
            mRGBABuffer = ByteBuffer.allocate(mImageHeight * mImageWidth * 4);
        }

        if (mBeautifyTextureId == null) {
            mBeautifyTextureId = new int[1];
            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mBeautifyTextureId, GLES20.GL_TEXTURE_2D);
        }

        if (mTextureOutId == null) {
            mTextureOutId = new int[1];
            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mTextureOutId, GLES20.GL_TEXTURE_2D);
        }

        if (mVideoEncoderTexture == null) {
            mVideoEncoderTexture = new int[1];
        }

        if(mSurfaceTexture != null && !mIsPaused){
            mSurfaceTexture.updateTexImage();
        }else{
            return;
        }

        mStartTime = System.currentTimeMillis();
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mRGBABuffer.rewind();

        long preProcessCostTime = System.currentTimeMillis();
        int textureId = mGLRender.preProcess(mTextureId, mRGBABuffer);
//        LogUtils.i(TAG, "preprocess cost time: %d", System.currentTimeMillis() - preProcessCostTime);

        int result = -1;

        if(!mShowOriginal){

            if(mNeedObject) {
                if (mNeedSetObjectTarget) {
                    long startTimeSetTarget = System.currentTimeMillis();

                    STRect inputRect = new STRect(mTargetRect.left, mTargetRect.top, mTargetRect.right, mTargetRect.bottom);

                    mSTMobileObjectTrackNative.setTarget(mRGBABuffer.array(), STCommon.ST_PIX_FMT_RGBA8888, mImageWidth, mImageHeight, inputRect);
//                    LogUtils.i(TAG, "setTarget cost time: %d", System.currentTimeMillis() - startTimeSetTarget);
                    mNeedSetObjectTarget = false;
                    mIsObjectTracking = true;
                }

                Rect rect = new Rect(0, 0, 0, 0);

                if (mIsObjectTracking) {
                    long startTimeObjectTrack = System.currentTimeMillis();
                    float[] score = new float[1];
                    STRect outputRect = mSTMobileObjectTrackNative.objectTrack(mRGBABuffer.array(), STCommon.ST_PIX_FMT_RGBA8888, mImageWidth, mImageHeight,score);
//                    LogUtils.i(TAG, "objectTrack cost time: %d", System.currentTimeMillis() - startTimeObjectTrack);
                    mObjectCost = System.currentTimeMillis() - startTimeObjectTrack;

                    if(outputRect != null && score != null && score.length >0){
                        rect = STUtils.adjustToScreenRectMin(outputRect.getRect(), mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);
                    }

                    Message msg = mHandler.obtainMessage(PublisherActivity.MSG_DRAW_OBJECT_IMAGE);
                    msg.obj = rect;
                    mHandler.sendMessage(msg);
                    mIndexRect = rect;
                }else{
                    if (mNeedShowRect) {
                        Message msg = mHandler.obtainMessage(PublisherActivity.MSG_DRAW_OBJECT_IMAGE_AND_RECT);
                        msg.obj = mIndexRect;
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = mHandler.obtainMessage(PublisherActivity.MSG_DRAW_OBJECT_IMAGE);
                        msg.obj = rect;
                        mHandler.sendMessage(msg);
                        mIndexRect = rect;
                    }
                }
            }else{
                mObjectCost = 0;

                if(!mNeedObject || !(mNeedBeautify || mNeedSticker)){
                    Message msg = mHandler.obtainMessage(PublisherActivity.MSG_CLEAR_OBJECT);
                    mHandler.sendMessage(msg);
                }
            }

            if((mNeedBeautify || mNeedSticker) && mIsCreateHumanActionHandleSucceeded) {

                long startHumanAction = System.currentTimeMillis();
                STHumanAction humanAction = mSTHumanActionNative.humanActionDetect(mRGBABuffer.array(), STCommon.ST_PIX_FMT_RGBA8888,
                        mDetectConfig, getCurrentOrientation(), mImageWidth, mImageHeight);
//                LogUtils.i(TAG, "human action cost time: %d", System.currentTimeMillis() - startHumanAction);

                if(DEBUG){
                    if(humanAction != null && humanAction.faceCount > 0){
                        long expressionStartTime = System.currentTimeMillis();
                        mFaceExpressionResult = mSTHumanActionNative.getExpression(humanAction, getCurrentOrientation(), mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT);
//                        LogUtils.i(TAG, "face expression cost time: %d", System.currentTimeMillis() - expressionStartTime);

                        Message msg = mHandler.obtainMessage(PublisherActivity.MSG_UPDATE_FACE_EXPRESSION_INFO);
                        mHandler.sendMessage(msg);
                    }else {
                        mFaceExpressionResult = null;
                    }
                }

                /**
                 * DEBUG模式，测试使用
                 */
                if(DEBUG){
                    /**
                     * DEBUG模式下，打印segment和handAction结果，测试使用
                     */
                    if(humanAction != null){
                        if(humanAction.getImage() != null){
//                            LogUtils.i(TAG, "human action background result: %d", 1);
                        }else{
//                            LogUtils.i(TAG, "human action background result: %d", 0);
                        }

                        if(humanAction.hands != null && humanAction.hands.length > 0){
                            mHandAction = humanAction.hands[0].handAction;

                            Message msg = mHandler.obtainMessage(PublisherActivity.MSG_UPDATE_HAND_ACTION_INFO);
                            mHandler.sendMessage(msg);
                        }else {
                            mHandAction = 0;
                            Message msg = mHandler.obtainMessage(PublisherActivity.MSG_RESET_HAND_ACTION_INFO);
                            mHandler.sendMessage(msg);
                        }
                    }

                    /**
                     * DEBUG模式下，每20帧计算一次人脸属性值，需要先加载人脸属性model和创建句柄，测试使用
                     */
                    if(mFrameCount <= 20){
                        mFrameCount++;
                    }else{
                        mFrameCount = 0;
                        faceAttributeDetect(mRGBABuffer.array(), humanAction);//do face attribute
                    }

                    /**
                     * DEBUG模式下，将240、眼球轮廓和中心点、肢体关键点使用opengl绘制到屏幕，测试使用
                     */
                    if(humanAction != null){
                        if(humanAction.faceCount > 0){
                            for(int i = 0; i < humanAction.faceCount; i++){
                                float[] points = STUtils.getExtraPoints(humanAction, i, mImageWidth, mImageHeight);
                                if(points != null && points.length > 0){
                                    mGLRender.onDrawPoints(textureId, points);
                                }
                            }
                        }

                        if(humanAction.bodyCount > 0){
                            for(int i = 0; i < humanAction.bodyCount; i++){
                                float[] points = STUtils.getBodyKeyPoints(humanAction, i, mImageWidth, mImageHeight);
                                if(points != null && points.length > 0){
                                    mGLRender.onDrawPoints(textureId, points);
                                }
                            }

                            //print body[0] action
                            mBodyAction = humanAction.bodys[0].bodyAction;
//                            LogUtils.i(TAG, "human action body count: %d", humanAction.bodyCount);
//                            LogUtils.i(TAG, "human action body[0] action: %d", humanAction.bodys[0].bodyAction);

                            Message msg = mHandler.obtainMessage(PublisherActivity.MSG_UPDATE_BODY_ACTION_INFO);
                            mHandler.sendMessage(msg);
                        }else {
                            mBodyAction = 0;
                        }

                        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                    }
                }

                STMobile106[] arrayFaces = null, arrayOutFaces = null;
                int orientation = getCurrentOrientation();


                //美颜
                if (mNeedBeautify) {// do beautify
                    long beautyStartTime = System.currentTimeMillis();
                    result = mStBeautifyNative.processTexture(textureId, mImageWidth, mImageHeight, humanAction, mBeautifyTextureId[0], mHumanActionBeautyOutput);
                    long beautyEndTime = System.currentTimeMillis();
//                    LogUtils.i(TAG, "beautify cost time: %d", beautyEndTime-beautyStartTime);
                    if (result == 0) {
                        textureId = mBeautifyTextureId[0];
                        humanAction = mHumanActionBeautyOutput;
//                        LogUtils.i(TAG, "replace enlarge eye and shrink face action");
                    }
                }

                //调用贴纸API绘制贴纸
                if(mNeedSticker){
                    /**
                     * 1.在切换贴纸时，调用STMobileStickerNative的changeSticker函数，传入贴纸路径(参考setShowSticker函数的使用)
                     * 2.切换贴纸后，使用STMobileStickerNative的getTriggerAction函数获取当前贴纸支持的手势和前后背景等信息，返回值为int类型
                     * 3.根据getTriggerAction函数返回值，重新配置humanActionDetect函数的config参数，使detect更高效
                     *
                     * 例：只检测人脸信息和当前贴纸支持的手势等信息时，使用如下配置：
                     * mDetectConfig = mSTMobileStickerNative.getTriggerAction()|STMobileHumanActionNative.ST_MOBILE_FACE_DETECT;
                    */
                    boolean needOutputBuffer = false; //如果需要输出buffer推流或其他，设置该开关为true
                    int frontStickerOrientation = 0;//前景贴纸方向
                    long stickerStartTime = System.currentTimeMillis();
                    if (!needOutputBuffer) {
                        result = mStStickerNative.processTexture(textureId, humanAction, orientation, mImageWidth, mImageHeight,
                                frontStickerOrientation, false, mTextureOutId[0]);
                    } else {  //如果需要输出buffer用作推流等
                        byte[] imageOut = new byte[mImageWidth * mImageHeight * 4];
                        result = mStStickerNative.processTextureAndOutputBuffer(textureId, humanAction, orientation, mImageWidth,
                                mImageHeight, frontStickerOrientation, false, mTextureOutId[0], STCommon.ST_PIX_FMT_RGBA8888, imageOut);
                    }

//                    LogUtils.i(TAG, "processTexture result: %d", result);
//                    LogUtils.i(TAG, "sticker cost time: %d", System.currentTimeMillis() - stickerStartTime);

                    if (result == 0) {
                        textureId = mTextureOutId[0];
                    }
                }
            }

            if(mCurrentFilterStyle != mFilterStyle){
                mCurrentFilterStyle = mFilterStyle;
                mSTMobileStreamFilterNative.setStyle(mCurrentFilterStyle);
            }
            if(mCurrentFilterStrength != mFilterStrength){
                mCurrentFilterStrength = mFilterStrength;
                mSTMobileStreamFilterNative.setParam(STFilterParamsType.ST_FILTER_STRENGTH, mCurrentFilterStrength);
            }

            if(mFilterTextureOutId == null){
                mFilterTextureOutId = new int[1];
                GlUtil.initEffectTexture(mImageWidth, mImageHeight, mFilterTextureOutId, GLES20.GL_TEXTURE_2D);
            }

            //滤镜
            if(mNeedFilter){
                long filterStartTime = System.currentTimeMillis();
                int ret = mSTMobileStreamFilterNative.processTexture(textureId, mImageWidth, mImageHeight, mFilterTextureOutId[0]);
//                LogUtils.i(TAG, "filter cost time: %d", System.currentTimeMillis() - filterStartTime);
                if(ret == 0){
                    textureId = mFilterTextureOutId[0];
                }
            }

//            LogUtils.i(TAG, "frame cost time total: %d", System.currentTimeMillis() - mStartTime + mRotateCost + mObjectCost + mFaceAttributeCost/20);
        }


        if(mNeedSave) {
            savePicture(textureId);
            mNeedSave = false;
        }

        //video capturing
        if(mVideoEncoder != null){
            GLES20.glFinish();

            mVideoEncoderTexture[0] = textureId;
            mSurfaceTexture.getTransformMatrix(mStMatrix);
            processStMatrix(mStMatrix, mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT,
                    mCameraID == Camera.CameraInfo.CAMERA_FACING_BACK && mCameraProxy.getOrientation() == 270);

            synchronized (this) {
                if (mVideoEncoder != null) {
                    if(mNeedResetEglContext){
                        mVideoEncoder.setEglContext(EGL14.eglGetCurrentContext(), mVideoEncoderTexture[0]);
                        mNeedResetEglContext = false;
                    }
                    mVideoEncoder.frameAvailableSoon(mStMatrix);
                }
            }
        }

        mFrameCost = (int)(System.currentTimeMillis() - mStartTime + mRotateCost + mObjectCost + mFaceAttributeCost/20);

        long timer  = System.currentTimeMillis();
        mCount++;
        if(mIsFirstCount){
            mCurrentTime = timer;
            mIsFirstCount = false;
        }else{
            int cost = (int)(timer - mCurrentTime);
            if(cost >= 1000){
                mCurrentTime = timer;
                mFps = (((float)mCount *1000)/cost);
                mCount = 0;
            }
        }

//        LogUtils.i(TAG, "render fps: %f", mFps);

        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);

        mGLRender.onDrawFrame(textureId);
    }

    private void savePicture(int textureId) {
        ByteBuffer mTmpBuffer = ByteBuffer.allocate(mImageHeight * mImageWidth * 4);
        mGLRender.saveTextureToFrameBuffer(textureId, mTmpBuffer);

        mTmpBuffer.position(0);
        Message msg = Message.obtain(mHandler);
        msg.what = PublisherActivity.MSG_SAVING_IMG;
        msg.obj = mTmpBuffer;
        Bundle bundle = new Bundle();
        bundle.putInt("imageWidth", mImageWidth);
        bundle.putInt("imageHeight", mImageHeight);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    private int getCurrentOrientation() {
        int dir = Accelerometer.getDirection();
        int orientation = dir - 1;
        if (orientation < 0) {
            orientation = dir ^ 3;
        }

        return orientation;
    }

    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            if (!mCameraChanging) {
                mGlSurfaceView.requestRender();
            }
        }
    };

    /**
     * camera设备startPreview
     */
    private void setUpCamera() {
        // 初始化Camera设备预览需要的显示区域(mSurfaceTexture)
        if (mTextureId == OpenGLUtils.NO_TEXTURE) {
            mTextureId = OpenGLUtils.getExternalOESTextureID();
            mSurfaceTexture = new SurfaceTexture(mTextureId);
            mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
        }

        String size = mSupportedPreviewSizes.get(mCurrentPreview);
        int index = size.indexOf('x');
        mImageHeight = Integer.parseInt(size.substring(0, index));
        mImageWidth = Integer.parseInt(size.substring(index + 1));

        if(mIsPaused)
            return;

        while(!mSetPreViewSizeSucceed){
            try{
                mCameraProxy.setPreviewSize(mImageHeight, mImageWidth);
                mSetPreViewSizeSucceed = true;
            }catch (Exception e){
                mSetPreViewSizeSucceed = false;
            }

            try{
                Thread.sleep(10);
            }catch (Exception e){

            }
        }

        boolean flipHorizontal = mCameraProxy.isFlipHorizontal();
        mGLRender.adjustTextureBuffer(mCameraProxy.getOrientation(), flipHorizontal);

        if(mIsPaused)
            return;
        mCameraProxy.startPreview(mSurfaceTexture, null);
    }

    public void setShowSticker(String sticker) {
        mChangeStickerManagerHandler.removeMessages(MESSAGE_NEED_CHANGE_STICKER);
        Message msg = mChangeStickerManagerHandler.obtainMessage(MESSAGE_NEED_CHANGE_STICKER);
        msg.obj = sticker;

        mChangeStickerManagerHandler.sendMessage(msg);
    }

    public void setFilterStyle(String modelPath) {
        mFilterStyle = modelPath;
    }

    public void setFilterStrength(float strength){
        mFilterStrength = strength;
    }

    public long getStickerTriggerAction(){
        return mStStickerNative.getTriggerAction();
    }

    public void onResume() {
//        LogUtils.i(TAG, "onResume");

        if (mCameraProxy.getCamera() == null) {
            if (mCameraProxy.getNumberOfCameras() == 1) {
                mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            mCameraProxy.openCamera(mCameraID);
            mSupportedPreviewSizes = mCameraProxy.getSupportedPreviewSize(new String[]{"640x480", "1280x720"});
        }
        mIsPaused = false;
        mSetPreViewSizeSucceed = false;
        mNeedResetEglContext = true;

        mGLRender = new STGLRender();

        mGlSurfaceView.onResume();
        mGlSurfaceView.forceLayout();
        mGlSurfaceView.requestRender();
    }

    public void onPause() {
//        LogUtils.i(TAG, "onPause");
        //mCurrentSticker = null;
        mIsPaused = true;
        mSetPreViewSizeSucceed = false;
        mCameraProxy.releaseCamera();
//        LogUtils.d(TAG, "Release camera");

        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mSTHumanActionNative.reset();

                mStBeautifyNative.destroyBeautify();
                mStStickerNative.removeAvatarModel();
                mStStickerNative.destroyInstance();
                mSTMobileStreamFilterNative.destroyInstance();
                mRGBABuffer = null;
                deleteTextures();
                if(mSurfaceTexture != null){
                    mSurfaceTexture.release();
                }
                mGLRender.destroyFrameBuffers();
            }
        });

        mGlSurfaceView.onPause();
    }

    public void onDestroy() {
        //必须释放非opengGL句柄资源,负责内存泄漏
        synchronized (mHumanActionHandleLock) {
            mSTHumanActionNative.destroyInstance();
        }
        mSTFaceAttributeNative.destroyInstance();
        mSTMobileObjectTrackNative.destroyInstance();
    }

    /**
     * 释放纹理资源
     */
    protected void deleteTextures() {
//        LogUtils.i(TAG, "delete textures");
        deleteCameraPreviewTexture();
        deleteInternalTextures();
    }

    // must in opengl thread
    private void deleteCameraPreviewTexture() {
        if (mTextureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{
                    mTextureId
            }, 0);
        }
        mTextureId = OpenGLUtils.NO_TEXTURE;
    }

    private void deleteInternalTextures() {
        if (mBeautifyTextureId != null) {
            GLES20.glDeleteTextures(1, mBeautifyTextureId, 0);
            mBeautifyTextureId = null;
        }

        if (mTextureOutId != null) {
            GLES20.glDeleteTextures(1, mTextureOutId, 0);
            mTextureOutId = null;
        }

        if(mFilterTextureOutId != null){
            GLES20.glDeleteTextures(1, mFilterTextureOutId, 0);
            mFilterTextureOutId = null;
        }

        if(mVideoEncoderTexture != null){
            GLES20.glDeleteTextures(1, mVideoEncoderTexture, 0);
            mVideoEncoderTexture = null;
        }
    }

    public void switchCamera() {
        if (Camera.getNumberOfCameras() == 1
                || mCameraChanging) {
            return;
        }

        if(mCameraProxy.cameraOpenFailed()){
            return;
        }

        mCameraID = 1 - mCameraID;
        mCameraChanging = true;
        mCameraProxy.openCamera(mCameraID);

        mSetPreViewSizeSucceed = false;

        if(mNeedObject){
            resetIndexRect();
        }else{
            Message msg = mHandler.obtainMessage(PublisherActivity.MSG_CLEAR_OBJECT);
            mHandler.sendMessage(msg);
        }

        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                deleteTextures();
                if (mCameraProxy.getCamera() != null) {
                    setUpCamera();
                }
                mCameraChanging = false;
            }
        });
        mGlSurfaceView.requestRender();
    }

    public int getCameraID(){
        return mCameraID;
    }

    public void changePreviewSize(int currentPreview) {
        if (mCameraProxy.getCamera() == null || mCameraChanging
                || mIsPaused) {
            return;
        }

        mCurrentPreview = currentPreview;
        mSetPreViewSizeSucceed = false;
        mIsChangingPreviewSize = true;

        mCameraChanging = true;
        mCameraProxy.stopPreview();
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mRGBABuffer != null) {
                    mRGBABuffer.clear();
                }
                mRGBABuffer = null;

                deleteTextures();
                if (mCameraProxy.getCamera() != null) {
                    setUpCamera();
                }

                mGLRender.init(mImageWidth, mImageHeight);
                if(DEBUG){
                    mGLRender.initDrawPoints();
                }

                if(mNeedObject){
                    resetIndexRect();
                }

                mGLRender.calculateVertexBuffer(mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);
                if (mListener != null) {
                    mListener.onChangePreviewSize(mImageHeight, mImageWidth);
                }

                mCameraChanging = false;
                mIsChangingPreviewSize = false;
                mGlSurfaceView.requestRender();
//                LogUtils.d(TAG, "exit  change Preview size queue event");
            }
        });
    }

    public void enableObject(boolean enabled){
        mNeedObject = enabled;

        if(mNeedObject){
            resetIndexRect();
        }
    }

    public void setIndexRect(int x, int y, boolean needRect){
        mIndexRect = new Rect(x, y, x + mScreenIndexRectWidth, y + mScreenIndexRectWidth);
        mNeedShowRect = needRect;
    }

    public Rect getIndexRect(){
        return mIndexRect;
    }

    public void setObjectTrackRect(){
        mNeedSetObjectTarget = true;
        mIsObjectTracking = false;
        mTargetRect = STUtils.adjustToImageRectMin(getIndexRect(), mSurfaceWidth, mSurfaceHeight, mImageWidth,mImageHeight);
    }

    public void disableObjectTracking(){
        mIsObjectTracking = false;
    }

    public void resetObjectTrack(){
        mSTMobileObjectTrackNative.reset();
    }

    public void resetIndexRect(){
        if(mImageWidth == 0){
            return;
        }

        mScreenIndexRectWidth = mSurfaceWidth/4;

        mIndexRect.left = (mSurfaceWidth - mScreenIndexRectWidth)/2;
        mIndexRect.top = (mSurfaceHeight - mScreenIndexRectWidth)/2;
        mIndexRect.right = mIndexRect.left + mScreenIndexRectWidth;
        mIndexRect.bottom = mIndexRect.top + mScreenIndexRectWidth;

        mNeedShowRect = true;
        mNeedSetObjectTarget = false;
        mIsObjectTracking = false;
    }

    public int getPreviewWidth(){
        return mImageWidth;
    }

    public int getPreviewHeight(){
        return mImageHeight;
    }

    public void setVideoEncoder(final MediaVideoEncoder encoder) {

        mGlSurfaceView.queueEvent(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                synchronized (this) {
                    if (encoder != null) {
                        encoder.setEglContext(EGL14.eglGetCurrentContext(), mVideoEncoderTexture[0]);
                    }
                    mVideoEncoder = encoder;
                }
            }
        });
    }

    private void processStMatrix(float[] matrix, boolean needMirror, boolean needFlip){
        if(needMirror && matrix != null && matrix.length == 16){
            for(int i = 0; i < 3; i++){
                matrix[4 * i] = -matrix[4 * i];
            }

            if(matrix[4 * 3] == 0){
                matrix[4 * 3] = 1.0f;
            }else if(matrix[4 *3] == 1.0f){
                matrix[4 *3] = 0f;
            }
        }

        if(needFlip && matrix != null && matrix.length == 16){
            matrix[0] = 1.0f;
            matrix[5] = -1.0f;
            matrix[12] = 0f;
            matrix[13] = 1.0f;
        }

        return;
    }

    public int getFrameCost(){
        return mFrameCost;
    }

    public float getFpsInfo(){
        return (float)(Math.round(mFps * 10))/10;
    }

    public boolean isChangingPreviewSize(){
        return mIsChangingPreviewSize;
    }

    public void addSubModelByName(String modelName){
        Message msg = mSubModelsManagerHandler.obtainMessage(MESSAGE_ADD_SUB_MODEL);
        msg.obj = modelName;

        mSubModelsManagerHandler.sendMessage(msg);
    }

    public void removeSubModelByConfig(int Config){
        Message msg = mSubModelsManagerHandler.obtainMessage(MESSAGE_REMOVE_SUB_MODEL);
        msg.obj = Config;
        mSubModelsManagerHandler.sendMessage(msg);
    }

    public long getHandActionInfo(){
        return mHandAction;
    }

    public long getBodyActionInfo(){
        return mBodyAction;
    }

    public boolean[] getFaceExpressionInfo(){
        return mFaceExpressionResult;
    }

}
