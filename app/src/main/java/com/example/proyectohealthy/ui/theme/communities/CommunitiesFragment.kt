package com.example.proyectohealthy.ui.theme.communities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectohealthy.databinding.FragmentCommunitiesBinding
import com.example.proyectohealthy.model.Community
import com.google.firebase.firestore.FirebaseFirestore

class CommunitiesFragment : Fragment() {

    private var _binding: FragmentCommunitiesBinding? = null
    private val binding get() = _binding!!
    private lateinit var communityAdapter: CommunityAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCommunitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communityAdapter = CommunityAdapter()
        binding.recyclerViewCommunities.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = communityAdapter
        }

        loadCommunities()
    }

    private fun loadCommunities() {
        FirebaseFirestore.getInstance().collection("Comunidad")
            .orderBy("miembros", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    //manejo de errores
                    return@addSnapshotListener
                }

                val communities = snapshot?.toObjects(Community::class.java) ?: listOf()
                communityAdapter.submitList(communities)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}