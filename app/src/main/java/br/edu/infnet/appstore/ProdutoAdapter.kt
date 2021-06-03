package br.edu.infnet.appstore

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import br.edu.infnet.appstore.model.Produto
import com.google.firebase.firestore.FirebaseFirestore

class ProdutoAdapter(
    private var produtosList: MutableList<Produto>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore,
    val onClickListener: (Produto) -> Unit
)
    : RecyclerView.Adapter<ProdutoAdapter.ProdutosViewholder>(){

    private val TAG = "Adapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutosViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.produto_card, parent, false)

        return ProdutosViewholder(view)
    }

    class ProdutosViewholder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        var nomeProduto: TextView = itemView.findViewById(R.id.tv_produto)
        var preco: TextView = itemView.findViewById(R.id.tv_preco)
        var quantidade: TextView = itemView.findViewById(R.id.tv_quantidade)
        var descicao: TextView = itemView.findViewById(R.id.tv_descricao)
        var btnDelete: ImageView = itemView.findViewById(R.id.btn_delete)
        var avaliacao: TextView = itemView.findViewById(R.id.tv_rating)
        var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        var imgProduto: ImageView = itemView.findViewById(R.id.img_produto)
        var editProduto: LinearLayout = itemView.findViewById(R.id.edit_produto)
    }

    override fun onBindViewHolder(holder: ProdutosViewholder, position: Int) {
        val produto = produtosList[position]

        holder.nomeProduto.text = produto.nome.toString()
        holder.preco.text = produto.preco.toString()
        holder.quantidade.text = produto.quantidade.toString()
        holder.descicao.text = produto.descricao.toString()

        //holder.imgProduto = produto.image

        holder.ratingBar.rating = produto.avaliacao!!.toFloat()
        holder.avaliacao.text = produto.avaliacao.toString()

        holder.btnDelete.setOnClickListener {
            deleteProduto(produto, position)
        }

        holder.editProduto.setOnClickListener{
            onClickListener(produto)
        }
    }

    override fun getItemCount(): Int {
        return produtosList.size
    }

    private fun deleteProduto(produto: Produto, position: Int) {
        produto.id.let {
            firestoreDB.collection("produtos")
                .document(it)
                .delete()
                .addOnCompleteListener {
                    produtosList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, produtosList.size)
                    Toast.makeText(context, "Note has been deleted!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateProduto(id: String, produto: String?, preco: String?,
                              quantidade: String?, avaliacao: String?,
                              descricao: String?, imagem: String?) {
        val produto = Produto(id, produto, preco, quantidade, avaliacao, descricao, imagem)

        firestoreDB.collection("produtos")
            .document(produto.id)
            .set(produto.toMap())
            .addOnSuccessListener {
                Log.e(TAG, "Product document update successful!")
                Toast.makeText(context, "Product has been updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Product document", e)
                Toast.makeText(context, "Product could not be updated!", Toast.LENGTH_SHORT).show()
            }
    }
}