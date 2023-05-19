package io.fourth_finger.sound_sculptor

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.fourth_finger.sound_sculptor.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val numRows = 2
        val layoutManager = StaggeredGridLayoutManager(
            numRows,
            StaggeredGridLayoutManager.HORIZONTAL
        )
        binding.mainRecyclerView.layoutManager = layoutManager

        binding.mainRecyclerView.adapter = MainRecyclerViewAdapter()

        binding.mainRecyclerView.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                // TODO allow for the last item in one row to take up the whole row
                //  if the other row is wider due to more elements
                val layoutParams = view.layoutParams as
                        StaggeredGridLayoutManager.LayoutParams
                val rightPadding = 0
                outRect.set(0, 0, rightPadding, 0)
            }
        })
    }

}