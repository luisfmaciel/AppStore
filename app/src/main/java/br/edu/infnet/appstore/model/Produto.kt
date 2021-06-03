package br.edu.infnet.appstore.model

import java.util.HashMap

class Produto(
    val id: String = "",
    val nome: String? = "",
    val preco: String? = "0.00",
    val quantidade: String? = "0",
    var avaliacao: String? = "0",
    val descricao: String? = "",
    val image: String? = ""
) {

    fun toMap(): Map<String,String> {

        val result = HashMap<String,String>()
        result["id"] = id
        result["nome"] = nome.toString()
        result["preco"] = preco.toString()
        result["quantidade"] = quantidade.toString()
        result["avaliacao"] = avaliacao.toString()
        result["descricao"] = descricao.toString()
        result["image"] = image.toString()


        return result
    }

}