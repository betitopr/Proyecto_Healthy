package com.example.proyectohealthy.ui.theme.createpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyectohealthy.databinding.FragmentCreatePostBinding
import com.example.proyectohealthy.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import com.google.firebase.Timestamp

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreatePost.setOnClickListener {
            createPost()
        }
    }

    private fun createPost() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (title.isEmpty()) {
            binding.editTextTitle.error = "El título es obligatorio"
            return
        }

        if (content.isEmpty()) {
            binding.editTextContent.error = "Escribe algo ps sanas@"
            return
        }

        val authorId = currentUser?.uid ?: "anonymous_user"

        //por ahora se usará una ID de comunidad
        val defaultCommunityId = "default_community_id"

        val post = Post(
            title = title,
            content = content,
            authorId = authorId,
            communityId = defaultCommunityId,
            createdAt = Timestamp.now(),
            upvotes = 0,
            downvotes = 0,
            commentCount = 0,
            id = "",
        )

        //codigo de emergencia
        FirebaseFirestore.getInstance().collection("posts")
            .add(post)
            .addOnSuccessListener { documentReference ->
                // Asignar el ID generado al objeto post
                val updatedPost = post.copy(id = documentReference.id)
        //cod"""

                FirebaseFirestore.getInstance().collection("posts").document(updatedPost.id)
                    .set(updatedPost)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Post creado correctamente", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al actualizar el post con ID: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al crear el post: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
