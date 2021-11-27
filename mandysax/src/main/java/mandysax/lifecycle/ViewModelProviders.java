package mandysax.lifecycle;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * @author Administrator
 */
public final class ViewModelProviders {

    /**
     * @param owner Object that implements ViewModelStoreOwner
     * @return ViewModelLifecycle object
     */
    @NonNull
    @Contract("_ -> new")
    public static ViewModelLifecycle of(@NonNull ViewModelStoreOwner owner) {
        return new ViewModelLifecycle(owner.getViewModelStore(), null);
    }

    /**
     * @param owner   Object that implements ViewModelStoreOwner
     * @param factory ViewModelProviders.Factory object
     * @return ViewModelLifecycle object
     */
    @NonNull
    @Contract("_, _ -> new")
    public static ViewModelLifecycle of(@NonNull ViewModelStoreOwner owner, Factory factory) {
        return new ViewModelLifecycle(owner.getViewModelStore(), factory);
    }

    /**
     * @param viewModelStore Object that implements ViewModelStoreImpl
     * @param factory        ViewModelProviders.Factory object
     * @return ViewModelLifecycle object
     */
    @NonNull
    @Contract(value = "_, _ -> new", pure = true)
    public static ViewModelLifecycle of(ViewModelStore viewModelStore, Factory factory) {
        return new ViewModelLifecycle(viewModelStore, factory);
    }

    public static final class ViewModelLifecycle {

        private final ViewModelStore mViewModelStore;

        private final ViewModelProviders.Factory mFactory;

        /*
         *在2.0.0中ViewModelLifecycle不再需要FragmentActivity参数
         *只需要提供ViewModelStore,管理器便可工作
         */
        public ViewModelLifecycle(ViewModelStore viewModelStore, ViewModelProviders.Factory factory) {
            this.mViewModelStore = viewModelStore;
            this.mFactory = factory;
        }

        /**
         * @param name Object that extends ViewModel
         * @param <T>  extends ViewModel
         * @return Your ViewModel object
         */
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T get(Class<T> name) {
            ViewModel viewModel = mViewModelStore.get(name);
            if (viewModel == null) {
                viewModel = mFactory == null ? new ViewModelProviders.Factory() {
                }.create(name) : mFactory.create(name);
                mViewModelStore.put(name, viewModel);
            }
            return (T) viewModel;
        }

    }

    public interface Factory {

        /**
         * 返回构建的ViewModel
         *
         * @param modelClass The ViewModel class that needs to be create
         * @param <T>        extends ViewModel
         * @return Your ViewModel object
         */
        default <T extends ViewModel> T create(Class<T> modelClass) {
            try {
                return modelClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

