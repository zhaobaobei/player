package remix.myplayer.uri;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import remix.myplayer.APlayerApplication;
import remix.myplayer.R;
import remix.myplayer.lyric.network.RxUtil;
import remix.myplayer.model.mp3.Song;
import remix.myplayer.model.netease.NSearchRequest;
import remix.myplayer.util.Constants;
import remix.myplayer.util.ImageUriUtil;
import remix.myplayer.util.PlayListUtil;

import static remix.myplayer.util.ImageUriUtil.getSearchRequestWithAlbumType;

/**
 * Created by Remix on 2017/11/30.
 */

public class PlayListUriRequest extends LibraryUriRequest {

    public PlayListUriRequest(SimpleDraweeView image, NSearchRequest request,RequestConfig config) {
        super(image,request,config);
    }

    @Override
    public void load() {
        Observable.create((ObservableOnSubscribe<String>) e -> {
            File customImage = ImageUriUtil.getCustomThumbIfExist(mRequest.getID(), Constants.URL_PLAYLIST);
            if(customImage != null && customImage.exists()){
                e.onNext("file://" + customImage.getAbsolutePath());
            }
            e.onComplete();
        }).switchIfEmpty(new Observable<String>() {
            @Override
            protected void subscribeActual(Observer<? super String> observer) {
                //没有设置过封面，对于播放列表类型的查找播放列表下所有歌曲，直到有一首歌曲存在封面
                List<Song> songs = PlayListUtil.getMP3ListByIds(PlayListUtil.getIDList(mRequest.getID()));
                if(songs == null || songs.size() == 0){
                    observer.onError(new Throwable(APlayerApplication.getContext().getString(R.string.no_song)));
                    return;
                }
                Observable.fromIterable(songs)
                        .flatMap(song -> getThumbObservable(getSearchRequestWithAlbumType(song)))
                        .subscribe(s -> {
                            observer.onNext(s);
                            observer.onComplete();
                        }, throwable -> observer.onError(new Throwable(throwable.toString())));

            }
        }).compose(RxUtil.applyScheduler())
        .subscribe(this::onSuccess,throwable -> onError(throwable.toString()));
    }
}
