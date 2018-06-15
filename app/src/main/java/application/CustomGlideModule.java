package application;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by xdhwwdz20112163.com on 2018/3/1.
 */

public class CustomGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context,
                "glide_cache", 1024 * 1024 * 1024));
        builder.setMemoryCache(new LruResourceCache(1024 * 1024));
        builder.setBitmapPool(new LruBitmapPool(1024 * 1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
