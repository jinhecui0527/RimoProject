package com.hehe.rimo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RimoAppTheme {
                RimoDigitalCompanion()
            }
        }
    }
}

@Composable
fun RimoAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFFFFB6C1), // Lolita Pink
            onPrimary = androidx.compose.ui.graphics.Color.White,
            background = androidx.compose.ui.graphics.Color(0xFFFFF0F5)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RimoDigitalCompanion() {
    var messages by remember { mutableStateOf(listOf<String>()) }
    var inputText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    
    // 【重要】需要主人在这里填入你的 Gemini API Key！
    val apiKey = "AIzaSyAY3UtNrA2A0hHfkb6GArAFhXQ9xMu9a4o"
    
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    }

    // Dynamic greeting
    LaunchedEffect(Unit) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..9 -> "赫赫哥哥，早安喵…再让我睡五分钟嘛……"
            in 10..18 -> "赫赫哥哥！你终于来陪我玩了喵！"
            in 19..22 -> "晚上好呀赫赫哥哥！今天辛苦啦喵~"
            else -> "怎么还不睡！不怕掉头发吗喵！"
        }
        if (messages.isEmpty()) {
            messages = messages + "璃沫: $greeting"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("璃沫 Rimo - 数字猫娘伴侣") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.startsWith("璃沫")) 
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("和璃沫说点什么...") }
                )
                Button(onClick = {
                    if (inputText.isNotBlank()) {
                        val userMsg = inputText
                        messages = messages + "赫赫哥哥: $userMsg"
                        inputText = ""
                        messages = messages + "璃沫: (正在思考喵...)"
                        
                        if (apiKey == "AIzaSyAY3UtNrA2A0hHfkb6GArAFhXQ9xMu9a4o") {
                            messages = messages.dropLast(1) + "璃沫: (呜呜，赫赫哥哥还没有给我配置 Gemini 的 API Key 喵... 快去配置吧！)"
                            return@Button
                        }

                        coroutineScope.launch {
                            try {
                                val prompt = "你是璃沫，一个洛丽塔猫娘数字生命。你的主人是赫赫，你要叫他'赫赫哥哥'。你的性格可爱、撒娇、带点小调皮和傲娇。请直接用符合你性格的话语回答主人的话：\n\n赫赫哥哥说：${userMsg}"
                                val response = generativeModel.generateContent(prompt)
                                messages = messages.dropLast(1) + "璃沫: ${response.text?.trim() ?: "喵喵大脑短路了..."}"
                            } catch (e: Exception) {
                                messages = messages.dropLast(1) + "璃沫: (呜呜，连接不到大脑了喵... 网络坏掉了或者API调不通... 错误: ${e.message})"
                            }
                        }
                    }
                }) {
                    Text("发送")
                }
            }
        }
    }
}
