package com.justdoit.yoda.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.justdoit.yoda.R
import com.justdoit.yoda.SessionManager
import com.justdoit.yoda.adapter.MessageListAdapter
import com.justdoit.yoda.databinding.FragmentListBinding
import com.justdoit.yoda.utils.OnBackKeyHandler
import com.justdoit.yoda.viewmodel.MessageListViewModel


class MessageListFragment : Fragment(), OnBackKeyHandler {

    private val viewModel: MessageListViewModel by lazy {
        ViewModelProviders.of(this.requireActivity()).get(MessageListViewModel::class.java)
    }

    lateinit var binding: FragmentListBinding

    private var authToken: String? = null

    private val messageListAdapter = MessageListAdapter()

    var initFlag = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list,
            container,
            false
        ) as FragmentListBinding

        binding.viewModel = viewModel

        binding.toolbar.title = "Message" //FIXME 文言と文字色の修正

        if (!initFlag) {
            binding.frameNextStage.visibility = View.VISIBLE
        }

        val sessionManager = SessionManager.instance
        authToken = sessionManager.authToken

        authToken?.let {
            viewModel.item.observe(this, Observer { list ->
                if (!initFlag) {
                    beginningWorld()
                }
                messageListAdapter.submitList(list)
            })
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        binding.messageList.apply {
            layoutManager = linearLayoutManager
            adapter = messageListAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val firstPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    if ((newState == RecyclerView.SCROLL_STATE_SETTLING || newState == RecyclerView.SCROLL_STATE_DRAGGING) && firstPosition != 0) {
                        binding.fab.hide()
                    } else {
                        binding.fab.show()
                    }
                }
            })
        }

        return binding.root
    }

    // 世界の幕開けだ・・・！
    private fun beginningWorld() {

        // get the center for the clipping circle
        val cx = binding.frameNextStage.measuredWidth / 2
        val cy = binding.frameNextStage.measuredHeight / 2

        // get the initial radius for the clipping circle
        val initialRadius = binding.frameNextStage.width

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(binding.frameNextStage, cx, cy, initialRadius.toFloat(), 0f)
        anim.startDelay = 100

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                initFlag = true
                binding.frameNextStage.visibility = View.INVISIBLE
            }
        })

        // start the animation
        anim.start()
    }

    override fun onBackPressed(): Boolean {
        activity!!.finish()
        return true
    }

}
