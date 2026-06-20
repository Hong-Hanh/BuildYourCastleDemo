package com.honghanh.buildyourcastledemo.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.honghanh.buildyourcastledemo.R
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = viewModel(),
    onSignUpSuccess: () -> Unit, // Thêm callback điều hướng khi tạo tài khoản thành công
    onNavigateToLogin: () -> Unit
) {
    // 🔥 ĐÃ SỬA: Đọc trạng thái tải và thông báo lỗi trực tiếp từ AuthViewModel
    val isLoading by viewModel.isLoading
    val authErrorMessage by viewModel.errorMessage

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var localErrorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F0))
    ) {
        // Ảnh trang trí góc trái dưới
        Image(
            painter = painterResource(id = R.drawable.thungrac),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-20).dp, y = 20.dp),
            contentScale = ContentScale.Fit,
            alpha = 0.2f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 56.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "FocusHome",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B4332)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Create your account",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B4332)
            )

            Text(
                text = "Start building your focus castle today.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Input Tên
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text("YOUR NAME", color = Color(0xFFD1D5DB), fontSize = 13.sp)
                },
                label = { Text("Tên hiển thị") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1B4332),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedLabelColor = Color(0xFF1B4332),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Input Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text("YOUR@EMAIL.COM", color = Color(0xFFD1D5DB), fontSize = 13.sp)
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1B4332),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedLabelColor = Color(0xFF1B4332),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Input Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text("PASSWORD", color = Color(0xFFD1D5DB), fontSize = 13.sp)
                },
                label = { Text("Mật khẩu") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    Text(
                        text = if (showPassword) "Ẩn" else "Hiện",
                        fontSize = 12.sp,
                        color = Color(0xFF1B4332),
                        modifier = Modifier
                            .clickable { showPassword = !showPassword }
                            .padding(end = 12.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1B4332),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedLabelColor = Color(0xFF1B4332),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Input Xác nhận Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = {
                    Text("CONFIRM PASSWORD", color = Color(0xFFD1D5DB), fontSize = 13.sp)
                },
                label = { Text("Xác nhận mật khẩu") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (showConfirmPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    Text(
                        text = if (showConfirmPassword) "Ẩn" else "Hiện",
                        fontSize = 12.sp,
                        color = Color(0xFF1B4332),
                        modifier = Modifier
                            .clickable { showConfirmPassword = !showConfirmPassword }
                            .padding(end = 12.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (confirmPassword.isNotEmpty() && confirmPassword != password)
                        Color.Red else Color(0xFF1B4332),
                    unfocusedBorderColor = if (confirmPassword.isNotEmpty() && confirmPassword != password)
                        Color.Red else Color(0xFFD1D5DB),
                    focusedLabelColor = Color(0xFF1B4332),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            // Thông báo lỗi mật khẩu không khớp tại chỗ
            if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                Text(
                    text = "Mật khẩu không khớp",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            // 🔥 ĐÃ SỬA: Tổng hợp hiển thị thông báo lỗi (Lỗi Validate cục bộ hoặc Lỗi trả về từ Firebase)
            val finalErrorMessage = when {
                localErrorMessage.isNotBlank() -> localErrorMessage
                authErrorMessage.isNotBlank() -> authErrorMessage
                else -> ""
            }

            if (finalErrorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = finalErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nút Sign Up / Vòng tải dữ liệu
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF1B4332))
            } else {
                Button(
                    onClick = {
                        when {
                            name.trim().isEmpty() -> localErrorMessage = "Vui lòng nhập tên hiển thị"
                            email.trim().isEmpty() -> localErrorMessage = "Vui lòng nhập email"
                            password.length < 6 -> localErrorMessage = "Mật khẩu phải có ít nhất 6 ký tự"
                            password != confirmPassword -> localErrorMessage = "Mật khẩu xác nhận không khớp"
                            else -> {
                                localErrorMessage = ""
                                // 🔥 ĐÃ SỬA: Gọi hàm signUp từ AuthViewModel và truyền lambda chuyển đổi màn hình
                                viewModel.signUp(
                                    name = name.trim(),
                                    email = email.trim(),
                                    password = password,
                                    onSuccess = { onSignUpSuccess() }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4332))
                ) {
                    Text(
                        text = "Sign Up",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log In link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "Log In",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B4332),
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    BuildYourCastleDemoTheme {
        SignUpScreen(
            onSignUpSuccess = {},
            onNavigateToLogin = {}
        )
    }
}