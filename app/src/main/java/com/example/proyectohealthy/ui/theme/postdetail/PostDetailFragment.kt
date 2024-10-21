package com.example.proyectohealthy.ui.theme.postdetail

/*import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectohealthy.R
import com.example.proyectohealthy.databinding.FragmentPostDetailBinding
import com.example.proyectohealthy.model.Post
import com.example.proyectohealthy.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PostDetailFragmentArgs by navArgs()
    private lateinit var post: Post
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCommentRecyclerView()
        loadPost()
    }

    private fun setupCommentRecyclerView() {
        commentAdapter = CommentAdapter()
        binding.recyclerViewComments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }
    }

    private fun loadPost() {
        val postId = args.postId
        Log.d("PostDetailFragment", "Post ID: $postId")

        if (postId.isNotEmpty()) {
            FirebaseFirestore.getInstance().collection("posts").document(postId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        post = document.toObject(Post::class.java)!!.copy(id = document.id)
                        updateUI()
                    } else {
                        Toast.makeText(context, "Publicación no encontrada", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error al cargar la publicación: ${exception.message}", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
        } else {
            Toast.makeText(context, "El ID de la publicación está vacío", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun updateUI() {
        binding.textViewTitle.text = post.title
        binding.textViewContent.text = post.content
        binding.textViewAuthor.text = "Publicado por u/${post.authorId}"
        binding.textViewVotes.text = (post.upvotes - post.downvotes).toString()
        binding.textViewComments.text = "${post.commentCount} comentarios"

        binding.buttonSubmitComment.setOnClickListener {
            submitComment()
        }

        loadComments()
    }

    private fun submitComment() {
        val commentText = binding.editTextComment.text.toString().trim()
        if (commentText.isNotEmpty()) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val comment = Comment(
                    postId = post.id,
                    authorId = currentUser.uid,
                    content = commentText,
                    createdAt = com.google.firebase.Timestamp.now(),
                    upvotes = 0,
                    downvotes = 0
                )

                FirebaseFirestore.getInstance().collection("comments")
                    .add(comment)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Comentario añadido", Toast.LENGTH_SHORT).show()
                        binding.editTextComment.text.clear()
                        loadComments()
                        updateCommentCount(1)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al añadir comentario: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Debes estar logeado para comentar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadComments() {
        FirebaseFirestore.getInstance().collection("comments")
            .whereEqualTo("postId", post.id)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val commentsList = documents.toObjects(Comment::class.java)
                commentAdapter.submitList(commentsList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar comentarios: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCommentCount(delta: Int) {
        FirebaseFirestore.getInstance().collection("posts").document(post.id)
            .update("commentCount", post.commentCount + delta)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_post_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                editPost()
                true
            }
            R.id.action_delete -> {
                deletePost()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editPost() {
        val action = PostDetailFragmentDirections.actionPostDetailFragmentToEditPostFragment(post.id)
        findNavController().navigate(action)
    }

    private fun deletePost() {
        FirebaseFirestore.getInstance().collection("posts").document(post.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar la publicación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/
