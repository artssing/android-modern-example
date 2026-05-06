package com.artssing.androidmodernexampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.artssing.androidmodernexampleapp.ui.theme.AndroidModernExampleAppTheme
import com.artssing.androidmodernexampleapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidModernExampleAppTheme {
                // 將內容移至獨立組件，避免在 setContent 頂層讀取 innerPadding
                MainAppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    // 1. 定義滾動行為 (pinnedScrollBehavior 會讓 Bar 固定在頂部，但偵測滾動來變色)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            // 2. 將滾動連結到 Scaffold，這樣當內部的內容捲動時，TopAppBar 才會收到訊號
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Android Modern Example") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,        // 初始背景色
                    scrolledContainerColor = Color.Red,  // 捲動後的背景色 (例如變成紅色)
                    titleContentColor = Color.White
                ),
                scrollBehavior = scrollBehavior // 3. 綁定行為
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavigation()
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    // 確保 Lambda 在重組時保持不變
    val onLoginSuccess = remember(navController) {
        {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = onLoginSuccess)
        }
        composable("home") {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    // 為了展示變色效果，我們讓首頁內容變長並可以捲動
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "首頁 (向下捲動試試)",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 32.dp)
        )
        
        // 產生一些內容讓頁面可以捲動
        repeat(50) { index ->
            Text(
                text = "這是第 $index 筆資料",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AndroidModernExampleAppTheme {
        LoginScreen({

        })
    }
}

@Composable
fun LoginUserNameContent(
    usernameProvider: () -> String,
    onUsernameChange: (String) -> Unit,
    enabledProvider: () -> Boolean
) {
    TextField(
        value = usernameProvider(),
        onValueChange = onUsernameChange,
        label = { Text("帳號") },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabledProvider()
    )
}

@Composable
fun LoginPasswordContent(
    passwordProvider: () -> String,
    onPasswordChange: (String) -> Unit,
    enabledProvider: () -> Boolean
) {
    TextField(
        value = passwordProvider(),
        onValueChange = onPasswordChange,
        label = { Text("密碼") },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabledProvider()
    )
}

@Composable
fun LoadingOverlay(isVisibleProvider: () -> Boolean) {
    AnimatedVisibility(
        visible = isVisibleProvider(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun LoginSuccessObserver(
    isLoggedInProvider: () -> Boolean,
    onLoginSuccess: () -> Unit
) {
    val isLoggedIn = isLoggedInProvider()
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
) {
    // 將 ViewModel 獲取移入更小的 Scope，避免 hiltViewModel() 影響 LoginScreen 本身
    val viewModel: MainViewModel = hiltViewModel()

    LoginSuccessObserver(
        isLoggedInProvider = viewModel::isLoggedIn,
        onLoginSuccess = onLoginSuccess
    )

    LoginContent(
        usernameProvider = viewModel::username,
        passwordProvider = viewModel::password,
        isLoadingProvider = viewModel::isLoading,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::login
    )
}

@Composable
fun LoginContent(
    usernameProvider: () -> String,
    passwordProvider: () -> String,
    isLoadingProvider: () -> Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "歡迎回來", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(32.dp))

            LoginUserNameContent(
                usernameProvider = usernameProvider,
                onUsernameChange = onUsernameChange,
                enabledProvider = { !isLoadingProvider() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginPasswordContent(
                passwordProvider = passwordProvider,
                onPasswordChange = onPasswordChange,
                enabledProvider = { !isLoadingProvider() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            LoginButton(
                onClick = onLoginClick,
                isLoadingProvider = isLoadingProvider,
                usernameProvider = usernameProvider,
                passwordProvider = passwordProvider
            )

            VisibilityExample()
        }

        LoadingOverlay(isVisibleProvider = isLoadingProvider)
    }
}

@Composable
fun LoginButton(
    onClick: () -> Unit,
    isLoadingProvider: () -> Boolean,
    usernameProvider: () -> String,
    passwordProvider: () -> String
) {
    // 關鍵優化：在元件內部使用 derivedStateOf
    // 只有當計算出的「Boolean 結果」改變時，Button 才會重組
    val isEnabled by remember {
        derivedStateOf {
            !isLoadingProvider() && 
            usernameProvider().isNotEmpty() && 
            passwordProvider().isNotEmpty()
        }
    }

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled
    ) {
        Text("登入")
    }
}

@Composable
fun VisibilityExample() {
    // 1. 定義狀態：元件是否可見
    var isVisible by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        // 2. 設置觸發事件的按鈕
        Button(
            onClick = { isVisible = false }) {
            Text("點擊後讓下方的文字 GONE")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 根據狀態決定是否渲染元件
        // 如果 isVisible 為 false，這段代碼不會執行，元件就不存在於 UI 樹中
        if (isVisible) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = "我是會消失的文字",
                modifier = Modifier.background(Color.Yellow).padding(8.dp)
            )
        }

        Text("我是下方的內容，上方消失後我會自動補位")
    }
}