package com.example.proyectohealthy.ui.theme.editpost


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyectohealthy.databinding.FragmentEditPostBinding
import com.example.proyectohealthy.model.Post
import com.google.firebase.firestore.FirebaseFirestore

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!
    private val args: EditPostFragmentArgs by navArgs()
    private lateinit var post: Post

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPost()
        binding.buttonUpdatePost.setOnClickListener { updatePost() }
    }

    private fun loadPost() {
        FirebaseFirestore.getInstance().collection("posts").document(args.postId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    post = document.toObject(Post::class.java)!!
                    post = post.copy(id = document.id)//helado misterioso de la isla
                    binding.editTextTitle.setText(post.title)
                    binding.editTextContent.setText(post.content)
                } else {
                    Toast.makeText(context, "Post sin funcionar", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar el post: ${exception.message}", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
    }

    private fun updatePost() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()

        if (title.isNotEmpty() && content.isNotEmpty()) {
            val updatedPost = post.copy(
                title = title,
                content = content,
                updatedAt = com.google.firebase.Timestamp.now()
            )

            FirebaseFirestore.getInstance().collection("posts").document(post.id)
                .set(updatedPost)
                .addOnSuccessListener {
                    Toast.makeText(context, "Post actualizado correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error a actuaizar el post: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "El título y el contenido no pueden estar vacíos, entiende! >:C", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}