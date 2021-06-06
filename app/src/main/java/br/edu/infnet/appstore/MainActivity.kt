package br.edu.infnet.appstore

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.infnet.appstore.Util.EXTRA_AVALIACAO
import br.edu.infnet.appstore.Util.EXTRA_DESCRICAO
import br.edu.infnet.appstore.Util.EXTRA_ID
import br.edu.infnet.appstore.Util.EXTRA_IMAGEM
import br.edu.infnet.appstore.Util.EXTRA_PRECO
import br.edu.infnet.appstore.Util.EXTRA_PRODUTO
import br.edu.infnet.appstore.Util.EXTRA_QTD
import br.edu.infnet.appstore.Util.OPTION_SELECT
import br.edu.infnet.appstore.model.Produto
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.produto_card.*

class MainActivity : AppCompatActivity() {

    private var mAdapter: ProdutoAdapter? = null
    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private val TAG = "MainActivity"

    private val ADD_REQUEST_CODE = 71

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestoreDB = FirebaseFirestore.getInstance()

        loadNotesList()


        val produtsCollections = firestoreDB!!.collection("produtos")
        var filtro: Query? = produtsCollections
//        val query = firestoreDB!!.collection("produtos")
//        val registration = query.addSnapshotListener { snapshots, e ->
//            // ...
//        }
//
//        // ...
//
//        // Stop listening to changes
//        registration.remove()

        when(OPTION_SELECT) {
            0 -> filtro = produtsCollections.whereGreaterThan("quantidade", 0)
            1 -> filtro = produtsCollections.orderBy("avaliacao", Query.Direction.DESCENDING)
            2 -> filtro = produtsCollections.orderBy("preco", Query.Direction.DESCENDING)
            3 -> filtro = produtsCollections.orderBy("preco", Query.Direction.ASCENDING)

        }
        firestoreListener = filtro!!
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    return@EventListener
                }

                val produtosList = mutableListOf<Produto>()
                for (doc in documentSnapshots!!) {
                    Log.e("MainActivity", doc.id, e)
                    val produto = doc.toObject(Produto::class.java)
                    updateProduto(doc.id, produto.nome!!, produto.preco,
                    produto.quantidade!!, produto.avaliacao, produto.descricao!!,
                    produto.image!!)
                    produtosList.add(produto)
                }

                mAdapter = ProdutoAdapter(
                    produtosList,
                    applicationContext,
                    firestoreDB!!
                ) { partItem: Produto -> partItemClicked(partItem) }
                recyclerView_produtos.adapter = mAdapter
            })

        setUpListeners()
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_estoque -> {
                OPTION_SELECT = 0
            }
            R.id.item_melhorAvaliado -> {
                OPTION_SELECT = 1
            }
            R.id.item_maioresPrecos -> {
                OPTION_SELECT = 2
            }
            R.id.item_menoresPrecos -> {
                OPTION_SELECT = 3
            }
        }
        loadNotesList()
        return true
    }

    private fun partItemClicked(produto: Produto) {
        val intent = Intent(this, NovoProdutoActivity::class.java)
        intent.putExtra(EXTRA_ID, produto.id)
        intent.putExtra(EXTRA_PRODUTO, produto.nome)
        intent.putExtra(EXTRA_PRECO, produto.preco)
        intent.putExtra(EXTRA_QTD, produto.quantidade)
        intent.putExtra(EXTRA_AVALIACAO, produto.avaliacao)
        intent.putExtra(EXTRA_DESCRICAO, produto.descricao)
        intent.putExtra(EXTRA_IMAGEM, produto.image)
        startActivityForResult(intent, ADD_REQUEST_CODE)
    }


    private fun loadNotesList() {
        val produtsCollections = firestoreDB!!.collection("produtos")

        var filtro: Query? = produtsCollections
        Log.i("OPTION", OPTION_SELECT.toString())
        when(OPTION_SELECT) {
            0 -> filtro = produtsCollections.orderBy("quantidade", Query.Direction.DESCENDING)
            1 -> filtro = produtsCollections.orderBy("avaliacao", Query.Direction.DESCENDING)
            2 -> filtro = produtsCollections.orderBy("preco", Query.Direction.DESCENDING)
            3 -> filtro = produtsCollections.orderBy("preco", Query.Direction.ASCENDING)

        }

            filtro!!.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val notesList = mutableListOf<Produto>()

                    for (doc in task.result!!) {
                        val produto = doc.toObject(Produto::class.java)
                        Log.i("MainActivity", produto.preco.toString())
                        updateProduto(doc.id, produto.nome!!, produto.preco,
                            produto.quantidade!!, produto.avaliacao, produto.descricao!!,
                            produto.image!!)
                        notesList.add(produto)
                    }

                    mAdapter = ProdutoAdapter(notesList, applicationContext, firestoreDB!!) {
                            partItem: Produto -> partItemClicked(partItem) }
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    recyclerView_produtos.layoutManager = mLayoutManager
                    recyclerView_produtos.itemAnimator = DefaultItemAnimator()
                    recyclerView_produtos.adapter = mAdapter
//                    recyclerView_produtos.addItemDecoration(
//                        DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
//                    )
                } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
        //Apenas para verificar se inseriu
        //Log.i("Note", dbRoom!!.noteDao().all().size.toString())
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.addNote) {
//            val intent = Intent(this, NoteActivity::class.java)
//            startActivityForResult(intent, ADD_REQUEST_CODE)
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

    private fun setUpListeners(){
        fab_add.setOnClickListener {
            val intent = Intent(this, NovoProdutoActivity::class.java)
            startActivityForResult(intent, ADD_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data.let {
            val id = it!!.getStringExtra(EXTRA_ID)
            val nome = it.getStringExtra(EXTRA_PRODUTO).toString()
            val preco = it.getStringExtra(EXTRA_PRECO).toString()
            val quantidade = it.getStringExtra(EXTRA_QTD).toString()
            var avaliacao = it.getStringExtra(EXTRA_AVALIACAO).toString()
            val descricao = it.getStringExtra(EXTRA_DESCRICAO).toString()
            val imagem = it.getStringExtra(EXTRA_IMAGEM).toString()

            if (resultCode == Activity.RESULT_OK){
                if (requestCode == ADD_REQUEST_CODE) {
                    if (id == null) {
                        addProduto(id.toString(), nome, preco, quantidade, avaliacao, descricao, imagem)
                    } else {
                        updateProduto(id.toString(), nome, preco, quantidade, avaliacao, descricao, imagem)
                    }

                }
            }
        }

    }

    private fun updateProduto(id: String, produto: String?, preco: String?,
                              quantidade: String, avaliacao: String?,
                                descricao: String?, imagem: String?) {
        val produto = Produto(id, produto, preco, quantidade, avaliacao, descricao, imagem)

        firestoreDB!!.collection("produtos")
            .document(produto.id)
            .set(produto.toMap())
            .addOnSuccessListener {
                Log.e(TAG, "Product document update successful!")
                Toast.makeText(applicationContext, "Product has been updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Product document", e)
                Toast.makeText(applicationContext, "Product could not be updated!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addProduto(id: String, produto: String?, preco: String?,
                           quantidade: String?, avaliacao: String?,
                           descricao: String?, imagem: String?) {
        val produto = Produto(id, produto, preco, quantidade, avaliacao, descricao, imagem)

        firestoreDB!!.collection("produtos")
            .add(produto.toMap())
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                Toast.makeText(applicationContext, "Product has been added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Product document", e)
                Toast.makeText(applicationContext, "Product could not be added!", Toast.LENGTH_SHORT).show()
            }
    }
}