package studio.mandysa.music.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mandysax.anna2.callback.Callback
import mandysax.media.DefaultPlayerManager
import mandysax.media.model.DefaultArtist
import mandysax.media.model.DefaultMusic
import mandysax.navigation.Navigation
import studio.mandysa.jiuwo.utils.RecyclerViewUtils.addHeader
import studio.mandysa.jiuwo.utils.RecyclerViewUtils.addModels
import studio.mandysa.jiuwo.utils.RecyclerViewUtils.linear
import studio.mandysa.jiuwo.utils.RecyclerViewUtils.recyclerAdapter
import studio.mandysa.jiuwo.utils.RecyclerViewUtils.setup
import studio.mandysa.jiuwo.utils.RecyclerViewUtils.staggered
import studio.mandysa.music.R
import studio.mandysa.music.databinding.FragmentHomeBinding
import studio.mandysa.music.databinding.ItemPlaylistBinding
import studio.mandysa.music.databinding.ItemPlaylistHeadBinding
import studio.mandysa.music.databinding.ItemSongBinding
import studio.mandysa.music.logic.model.NeteaseCloudMusicApi
import studio.mandysa.music.logic.model.NewSongModel
import studio.mandysa.music.logic.model.PlaylistModel
import studio.mandysa.music.logic.utils.ArrayListUtils.createAlbum
import studio.mandysa.music.logic.utils.BindingAdapterUtils.getModels
import studio.mandysa.music.logic.utils.ClassUtils.create
import studio.mandysa.music.logic.utils.DefaultPlayManagerUtils.getInstance
import studio.mandysa.music.ui.all.playlist.PlaylistFragment
import studio.mandysa.music.ui.base.BaseFragment

class HomeFragment : BaseFragment() {

    private val mBinding: FragmentHomeBinding by bindView()

    public override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            homeList.linear().setup {
                addType<PlaylistModel>(R.layout.item_playlist_head)
                addType<NewSongModel>(R.layout.item_song)
                onBind {
                    when (itemViewType) {
                        R.layout.item_playlist_head -> {
                            ItemPlaylistHeadBinding.bind(itemView).playlistList.apply {
                                staggered(2, orientation = RecyclerView.HORIZONTAL).setup {
                                    addType<PlaylistModel.Playlist>(R.layout.item_playlist)
                                    onBind {
                                        val model = getModel<PlaylistModel.Playlist>()
                                        ItemPlaylistBinding.bind(itemView).apply {
                                            playlistTitle.text = model.name
                                            playlistCover.setImageURI(model.picUrl)
                                            playlistCover.setOnClickListener {
                                                Navigation.findViewNavController(it)
                                                    .navigate(
                                                        R.style.test,
                                                        PlaylistFragment(model.id!!)
                                                    )
                                            }
                                        }
                                    }
                                }
                                recyclerAdapter.models = getModel<PlaylistModel>().playlist!!
                            }
                        }
                        R.layout.item_song -> {
                            val model = getModel<NewSongModel>()
                            val modelPosition = this.modelPosition
                            ItemSongBinding.bind(itemView).apply {
                                songName.text = model.title
                                songSingerName.text = model.artist[0].name
                                songCover.setImageURI(model.coverUrl)
                                itemView.setOnClickListener {
                                    getInstance().apply {
                                        loadAlbum(
                                            getModels<DefaultMusic<DefaultArtist>>().createAlbum(),
                                            modelPosition
                                        )
                                        play()
                                    }
                                }
                                DefaultPlayerManager.getInstance()!!.changeMusicLiveData()
                                    .observe(viewLifecycleOwner) {
                                        if (it.equals(getModels<DefaultMusic<DefaultArtist>>().createAlbum()[modelPosition])) {
                                            cardView.setCardBackgroundColor(context.getColor(R.color.blue))
                                        } else cardView.setCardBackgroundColor(0)
                                    }
                            }
                        }
                    }
                }
            }
            statelayout.showLoading {
                NeteaseCloudMusicApi::class.java.create().apply {
                    recommendedPlaylist.set(object : Callback<PlaylistModel> {
                        override fun onResponse(t: PlaylistModel?) {
                            statelayout.showContentState()
                            mBinding.homeList.addHeader(t!!)
                        }

                        override fun onFailure(code: Int) {
                        }

                    })
                    recommendedSong.set(object : Callback<List<NewSongModel>> {
                        override fun onResponse(t: List<NewSongModel>?) {
                            statelayout.showContentState()
                            mBinding.homeList.addModels(t!!)
                        }

                        override fun onFailure(code: Int) {

                        }

                    })
                }
            }
            statelayout.showLoadingState()
        }
    }

}