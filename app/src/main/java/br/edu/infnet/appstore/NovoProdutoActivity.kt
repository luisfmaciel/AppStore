package br.edu.infnet.appstore

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import br.edu.infnet.appstore.Util.EXTRA_AVALIACAO
import br.edu.infnet.appstore.Util.EXTRA_DESCRICAO
import br.edu.infnet.appstore.Util.EXTRA_ID
import br.edu.infnet.appstore.Util.EXTRA_IMAGEM
import br.edu.infnet.appstore.Util.EXTRA_PRECO
import br.edu.infnet.appstore.Util.EXTRA_PRODUTO
import br.edu.infnet.appstore.Util.EXTRA_QTD
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_novo_produto.*

class NovoProdutoActivity : AppCompatActivity() {

    private var firestoreDB: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_produto)

        firestoreDB = FirebaseFirestore.getInstance()

        val mId = intent.getStringExtra(EXTRA_ID)
        val mNome = intent.getStringExtra(EXTRA_PRODUTO)
        val mPreco = intent.getStringExtra(EXTRA_PRECO)
        val mQuantidade = intent.getStringExtra(EXTRA_QTD)
        val mAvaliacao = intent.getStringExtra(EXTRA_AVALIACAO)
        val mDescricao = intent.getStringExtra(EXTRA_DESCRICAO)
        val mImagem = intent.getStringExtra(EXTRA_IMAGEM)

        if (mNome != null) {
            btn_salvar.text = getString(R.string.update)
            et_produto.setText(mNome.toString())
            et_preco.setText(mPreco.toString())
            et_quantidade.setText(mQuantidade.toString())
            et_descricao.setText(mDescricao.toString())
            et_url_image.setText(mImagem.toString())
            ratingBar_add.rating = mAvaliacao!!.toFloat()
            tv_rating_add.text = mAvaliacao.toString()
        }

        var avaliacao = "0"
        ratingBar_add.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            tv_rating_add.text = rating.toString()
            avaliacao = rating.toString()
        }

        btn_salvar.setOnClickListener {
            val retornoIntent = Intent()

            val nome = et_produto.text.toString()
            val preco = et_preco.text.toString()
            val quantidade = et_quantidade.text.toString()
            val descricao = et_descricao.text.toString()
            val imagem = et_url_image.text.toString()

            retornoIntent.putExtra(EXTRA_ID, mId)
            retornoIntent.putExtra(EXTRA_PRODUTO, nome)
            retornoIntent.putExtra(EXTRA_PRECO, preco)
            retornoIntent.putExtra(EXTRA_QTD, quantidade)
            retornoIntent.putExtra(EXTRA_AVALIACAO, avaliacao)
            retornoIntent.putExtra(EXTRA_DESCRICAO, descricao)
            retornoIntent.putExtra(EXTRA_IMAGEM, imagem)

            setResult(Activity.RESULT_OK, retornoIntent)
            finish()
        }
    }




}