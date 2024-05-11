package com.kakakobank.imagecollector.ui.search

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import com.kakakobank.imagecollector.base.viewmodel.EventViewModel
import com.kakakobank.imagecollector.model.Contents
import com.kakakobank.imagecollector.util.Log
import kotlin.random.Random

/**
 * [SearchFragment]
 */
class SearchViewModel: EventViewModel() {
    val itemList = MutableLiveData<List<Contents>>()
    val searchKeyword = MutableLiveData<String>()

    fun onTextChanged(editable: Editable) {
        val rand = Random(System.currentTimeMillis())
        itemList.value = mutableListOf<Contents>().apply {
            for(i in 0..rand.nextInt(1, 5)) {
                val contents = if (rand.nextBoolean()) {
                    Contents(
                        Contents.Type.IMAGE,
                        "https://search2.kakaocdn.net/argon/130x130_85_c/36hQpoTrVZp",
                        "Title",
                        "Category",
                        "url",
                        System.currentTimeMillis()
                    )
                } else {
                    Contents(
                        Contents.Type.VIDEO,
                        "asdf",
                        "dddd",
                        "ssss",
                        "ffff",
                        System.currentTimeMillis()
                    )
                }

                add(contents)
            }
        }
    }

    fun onClickFavorite(contents: Contents) {
        Log.e(contents)
    }

    fun onClickContents(contents: Contents) {
        Log.e(contents)
    }

    fun onClickDelete() {
        searchKeyword.value = ""
    }
}