package com.example.timetoshop.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun ProfileScreen(navController: NavHostController, profileViewModel: ProfileViewModel = viewModel()) {
    val user = profileViewModel.user
    val isLoading = profileViewModel.isLoading
    val hasShop = profileViewModel.hasShop

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = user?.username ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = user?.fullName?.take(2)?.uppercase() ?: "", fontSize = 40.sp)
                    }
                }
                Text(text = user?.fullName ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("0", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Posts")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = user?.followers?.size.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Followers")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = user?.following?.size.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Following")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (hasShop) {
                    Button(onClick = { navController.navigate("shop_profile") }) {
                        Icon(Icons.Default.Store, contentDescription = null)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Manage Your Shop")
                    }
                } else {
                    Button(onClick = { navController.navigate("create_shop") }) {
                        Icon(Icons.Default.AddBusiness, contentDescription = null)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Create a Shop")
                    }
                }
                OutlinedButton(onClick = { }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Edit Profile")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                profileViewModel.signOut()
                navController.navigate("login") {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text("Sign Out")
            }
        }
    }
}
