package studio.mandysa.music.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import studio.mandysa.music.databinding.FragmentSearchBinding
import studio.mandysa.music.ui.base.BaseFragment
import studio.mandysa.music.ui.searchhistory.SearchHistoryFragment

class SearchFragment : BaseFragment() {

    private val mBinding: FragmentSearchBinding by bindView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentPlusManager.beginTransaction().add(mBinding.show.id, SearchHistoryFragment()).commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("onDestroyView")
    }
}