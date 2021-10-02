package me.cniekirk.flex.util.precaching
//
//import android.graphics.drawable.Drawable
//import android.widget.AbsListView
//import coil.target.Target
//import java.util.*
//
//
//class ListPreloader<T>(
//    requestManager: RequestManager,
//    preloadModelProvider: PreloadModelProvider<T>,
//    preloadDimensionProvider: PreloadSizeProvider<T>,
//    maxPreload: Int
//) : AbsListView.OnScrollListener {
//    private val maxPreload: Int
//    private val preloadTargetQueue: PreloadTargetQueue
//    private val requestManager: RequestManager
//    private val preloadModelProvider: PreloadModelProvider<T>
//    private val preloadDimensionProvider: PreloadSizeProvider<T>
//    private var lastEnd = 0
//    private var lastStart = 0
//    private var lastFirstVisible = -1
//    private var totalItemCount = 0
//    private var isIncreasing = true
//
//    /**
//     * An implementation of PreloadModelProvider should provide all the models that should be
//     * preloaded.
//     *
//     * @param <U> The type of the model being preloaded.
//    </U> */
//    interface PreloadModelProvider<U> {
//        /**
//         * Returns a [List] of models that need to be loaded for the list to display adapter items
//         * in positions between `start` and `end`.
//         *
//         *
//         * A list of any size can be returned so there can be multiple models per adapter position.
//         *
//         *
//         * Every model returned by this method is expected to produce a valid [RequestBuilder]
//         * in [.getPreloadRequestBuilder]. If that's not possible for any set of models,
//         * avoid including them in the [List] returned by this method.
//         *
//         *
//         * Although it's acceptable for the returned [List] to contain `null` models,
//         * it's best to filter them from the list instead of adding `null` to avoid unnecessary
//         * logic and expanding the size of the [List]
//         *
//         * @param position The adapter position.
//         */
//        fun getPreloadItems(position: Int): List<U>
//
//        /**
//         * Returns a [RequestBuilder] for a given item on which [ ][RequestBuilder.load]} has been called or `null` if no valid load can be
//         * started.
//         *
//         *
//         * For the preloader to be effective, the [RequestBuilder] returned here must use
//         * exactly the same size and set of options as the [RequestBuilder] used when the ``View``
//         * is bound. You may need to specify a size in both places to ensure that the width and height
//         * match exactly. If so, you can use [ ][com.bumptech.glide.request.RequestOptions.override] to do so.
//         *
//         *
//         * The target and context will be provided by the preloader.
//         *
//         *
//         * If [RequestBuilder.load] is not called by this method, the preloader will
//         * trigger a [RuntimeException]. If you don't want to load a particular item or position,
//         * filter it from the list returned by [.getPreloadItems].
//         *
//         * @param item The model to load.
//         */
//        fun getPreloadRequestBuilder(item: U): RequestBuilder<*>?
//    }
//
//    /**
//     * An implementation of PreloadSizeProvider should provide the size of the view in the list where
//     * the resources will be displayed.
//     *
//     * @param <T> The type of the model the size should be provided for.
//    </T> */
//    interface PreloadSizeProvider<T> {
//        /**
//         * Returns the size of the view in the list where the resources will be displayed in pixels in
//         * the format [x, y], or `null` if no size is currently available.
//         *
//         *
//         * Note - The dimensions returned here must precisely match those of the view in the list.
//         *
//         *
//         * If this method returns `null`, then no request will be started for the given item.
//         *
//         * @param item A model
//         */
//        fun getPreloadSize(item: T, adapterPosition: Int, perItemPosition: Int): IntArray?
//    }
//
//    override fun onScrollStateChanged(absListView: AbsListView, scrollState: Int) {
//        // Do nothing.
//    }
//
//    override fun onScroll(
//        absListView: AbsListView, firstVisible: Int, visibleCount: Int, totalCount: Int
//    ) {
//        totalItemCount = totalCount
//        if (firstVisible > lastFirstVisible) {
//            preload(firstVisible + visibleCount, true)
//        } else if (firstVisible < lastFirstVisible) {
//            preload(firstVisible, false)
//        }
//        lastFirstVisible = firstVisible
//    }
//
//    private fun preload(start: Int, increasing: Boolean) {
//        if (isIncreasing != increasing) {
//            isIncreasing = increasing
//            cancelAll()
//        }
//        preload(start, start + if (increasing) maxPreload else -maxPreload)
//    }
//
//    private fun preload(from: Int, to: Int) {
//        var start: Int
//        var end: Int
//        if (from < to) {
//            start = Math.max(lastEnd, from)
//            end = to
//        } else {
//            start = to
//            end = Math.min(lastStart, from)
//        }
//        end = Math.min(totalItemCount, end)
//        start = Math.min(totalItemCount, Math.max(0, start))
//        if (from < to) {
//            // Increasing
//            for (i in start until end) {
//                preloadAdapterPosition(preloadModelProvider.getPreloadItems(i), i, true)
//            }
//        } else {
//            // Decreasing
//            for (i in end - 1 downTo start) {
//                preloadAdapterPosition(preloadModelProvider.getPreloadItems(i), i, false)
//            }
//        }
//        lastStart = start
//        lastEnd = end
//    }
//
//    private fun preloadAdapterPosition(items: List<T>, position: Int, isIncreasing: Boolean) {
//        val numItems = items.size
//        if (isIncreasing) {
//            for (i in 0 until numItems) {
//                preloadItem(items[i], position, i)
//            }
//        } else {
//            for (i in numItems - 1 downTo 0) {
//                preloadItem(items[i], position, i)
//            }
//        }
//    }
//
//    private fun preloadItem(item: T?, position: Int, perItemPosition: Int) {
//        if (item == null) {
//            return
//        }
//        val dimensions = preloadDimensionProvider.getPreloadSize(item, position, perItemPosition)
//            ?: return
//        val preloadRequestBuilder: RequestBuilder<Any> =
//            preloadModelProvider.getPreloadRequestBuilder(item) as RequestBuilder<Any>?
//                ?: return
//        preloadRequestBuilder.into(preloadTargetQueue.next(dimensions[0], dimensions[1]))
//    }
//
//    private fun cancelAll() {
//        for (i in preloadTargetQueue.queue.indices) {
//            requestManager.clear(preloadTargetQueue.next(0, 0))
//        }
//    }
//
//    private class PreloadTargetQueue internal constructor(size: Int) {
//        @com.sun.org.apache.bcel.internal.classfile.Synthetic
//        val queue: Queue<PreloadTarget>
//        fun next(width: Int, height: Int): PreloadTarget {
//            val result = queue.poll()
//            queue.offer(result)
//            result.photoWidth = width
//            result.photoHeight = height
//            return result
//        }
//
//        // The loop is short and the only point is to create the objects.
//        init {
//            queue = Util.createQueue(size)
//            for (i in 0 until size) {
//                queue.offer(PreloadTarget())
//            }
//        }
//    }
//
//    private class PreloadTarget : Target {
//
//        var photoHeight = 0
//        var photoWidth = 0
//
//        override fun onSuccess(result: Drawable) {
//
//            super.onSuccess(result)
//        }
//
//        fun getSize(cb: SizeReadyCallback) {
//            cb.onSizeReady(photoWidth, photoHeight)
//        }
//    }
//
//    /**
//     * Constructor for [com.bumptech.glide.ListPreloader] that accepts interfaces for providing
//     * the dimensions of images to preload, the list of models to preload for a given position, and
//     * the request to use to load images.
//     *
//     * @param preloadModelProvider Provides models to load and requests capable of loading them.
//     * @param preloadDimensionProvider Provides the dimensions of images to load.
//     * @param maxPreload Maximum number of items to preload.
//     */
//    init {
//        this.requestManager = requestManager
//        this.preloadModelProvider = preloadModelProvider
//        this.preloadDimensionProvider = preloadDimensionProvider
//        this.maxPreload = maxPreload
//        preloadTargetQueue = PreloadTargetQueue(maxPreload + 1)
//    }
//}