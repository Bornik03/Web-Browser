package com.example.browser
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.loadUrl("https://www.google.com")
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls=false
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccess = true
        setContent {
            MainContent(webView = webView)
        }

    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainContent(modifier: Modifier=Modifier,webView: WebView) {
    var url by remember { mutableStateOf("https://www.google.com") }
    var expanded by remember { mutableStateOf(false) }
    val progress = remember { mutableStateOf(false) }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val activity = LocalContext.current as? Activity
    backDispatcher?.addCallback(object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (webView.canGoBack()) {
                webView.goBack()
            }
            else
            {
                activity?.finish()
            }
        }
    })
    Box(modifier.fillMaxSize())
    {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                MyContent(url = url, webView = webView, progress = progress)
            }
            Card(modifier
                .fillMaxWidth()
            ) {
                Row {
                    IconButton(onClick = {expanded=true},modifier.padding(start = 30.dp)) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                    Spacer(modifier = modifier.padding(start = 10.dp))
                    IconButton(onClick = { webView.goBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                    Spacer(modifier = modifier.padding(start = 36.5.dp))
                    IconButton(onClick = {
                        if (url=="https://www.google.com")
                            url="www.google.com"
                        else if(url=="www.google.com")
                            url="https://"+"www.google.com"
                        else if (url=="https://www.bing.com")
                            url="www.bing.com"
                        else if(url=="www.bing.com")
                            url="https://"+"www.bing.com"
                        else if(url=="https://search.brave.com/")
                            url="https://search.brave.com"
                        else
                            url="https://"+"search.brave.com/"
                    },) {
                        Icon(Icons.Filled.Home, contentDescription = null)
                    }
                    Spacer(modifier = modifier.padding(start = 36.5.dp))
                    if (progress.value) {
                        CircularProgressIndicator(modifier.padding(3.dp))
                    }
                }
            }
        }
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded=false }) {
        DropdownMenuItem(text = { Row {
            Text(text = "Google")
            Spacer(modifier = modifier.padding(3.dp))
            if(url=="https://www.google.com"||url=="www.google.com")
            {
                Icon(Icons.Filled.Check, contentDescription = null)
            }
        } }, onClick = {url="https://www.google.com"
        expanded=false
        })
        DropdownMenuItem(text = { Row {
            Text(text = "Bing")
            Spacer(modifier = modifier.padding(3.dp))
            if(url=="https://www.bing.com"||url=="www.bing.com")
            {
                Icon(Icons.Filled.Check, contentDescription = null)
            }
        } }, onClick = {url="https://www.bing.com"
            expanded=false
        })
        DropdownMenuItem(text = {Row {
            Text(text = "Brave")
            Spacer(modifier = modifier.padding(3.dp))
            if(url=="https://search.brave.com/"||url=="https://search.brave.com")
            {
                Icon(Icons.Filled.Check, contentDescription = null)
            }
        } }, onClick = {url="https://search.brave.com/"
        expanded=false
        })
    }
}

@Composable
fun MyContent(url: String, webView: WebView, progress: MutableState<Boolean>) {
    AndroidView(factory = {
        webView.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = object :WebViewClient()
            {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progress.value=true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progress.value=false
                }
            }
        }
    }, update = {
        it.loadUrl(url)
    })
}