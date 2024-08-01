package com.gemini.chatbot

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.gemini.chatbot.adapter.GeminiAdapter
import com.gemini.chatbot.model.DataResponse
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var image: ImageView
    private lateinit var recyclerView: RecyclerView
    private var bitmap : Bitmap? = null
    private lateinit var imageUri: String
     var responseData = arrayListOf<DataResponse>()

    lateinit var adapter: GeminiAdapter

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri ->
        if(uri != null){

            imageUri = uri.toString()
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
            image.setImageURI(uri)
        }else{
            Log.d("Photopicker", "no media selected")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.ask_edit_text)
        button = findViewById(R.id.ask_button)
        image = findViewById(R.id.select_iv)
        recyclerView = findViewById(R.id.recycler_view_id)

        adapter = GeminiAdapter(this,responseData)
        recyclerView.adapter = adapter

        image.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        button.setOnClickListener {
            if(editText.text != null){
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = getString(R.string.api_key)
                )

                var prompt = editText.text.toString()
                editText.setText("")

                if(bitmap != null){
                    responseData.add(DataResponse(0, prompt, imageUri = imageUri))
                    adapter.notifyDataSetChanged()

                    val inputContent = content {
                        image(bitmap!!)
                        text(prompt)
                    }

                    GlobalScope.launch {
                        val response = generativeModel.generateContent(inputContent)

                        runOnUiThread {
                            responseData.add(DataResponse(1,response.text!!,""))
                            adapter.notifyDataSetChanged()
                        }

                    }


                }else{
                    responseData.add(DataResponse(0,prompt,""))
                    adapter.notifyDataSetChanged()

                    GlobalScope.launch {
                        bitmap = null
                        imageUri = ""
                        val response = generativeModel.generateContent(prompt)
                        runOnUiThread {
                            responseData.add(DataResponse(1,response.text!!,""))
                            adapter.notifyDataSetChanged()
                        }

                    }
                }
            }
        }
    }
}