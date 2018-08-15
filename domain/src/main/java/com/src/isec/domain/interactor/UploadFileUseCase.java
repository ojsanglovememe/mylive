package com.src.isec.domain.interactor;

import com.src.isec.domain.BaseParams;
import com.src.isec.domain.entity.BaseResponse;
import com.src.isec.domain.entity.FileEntity;
import com.src.isec.domain.repository.IUserRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author wj
 * @name IsecLive
 * @class name：com.src.isec.domain.interactor
 * @class describe
 * @time 2018/4/23 11:15
 * @change
 * @chang time
 * @class describe 上传图片
 */
public class UploadFileUseCase extends UseCase<BaseResponse<FileEntity>, UploadFileUseCase.Params> {

    private final IUserRepository mIUserRepository;

    @Inject
    public UploadFileUseCase(IUserRepository mIUserRepository) {
        this.mIUserRepository = mIUserRepository;
    }

    @Override
    protected Observable<BaseResponse<FileEntity>> buildUseCaseObservable(Params params) {
        return mIUserRepository.upload(params.uri);
    }

    public static UploadFileUseCase.Params builderParams() {
        return new UploadFileUseCase.Params();
    }

    public static final class Params extends BaseParams {
        String uri;

        public Params onFile(String uri) {
            this.uri = uri;
            return this;
        }
    }

}
