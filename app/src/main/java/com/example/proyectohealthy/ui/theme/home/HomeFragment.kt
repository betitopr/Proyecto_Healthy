package com.example.proyectohealthy.ui.theme.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectohealthy.databinding.FragmentHomeBinding
import com.example.proyectohealthy.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postAdapter = PostAdapter { post ->
            val action = HomeFragmentDirections.actionHomeFragmentToPostDetailFragment(post.id)
            findNavController().navigate(action)
        }

        binding.recyclerViewPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }

        loadPosts()
    }

    private fun loadPosts() {
        FirebaseFirestore.getInstance().collection("posts")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    //manejo de errorese
                    return@addSnapshotListener
                }

                val posts = snapshot?.toObjects(Post::class.java) ?: listOf()
                postAdapter.submitList(posts)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}