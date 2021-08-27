package com.company.firestoreexample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val personCollectionRef = Firebase.firestore.collection("persons")



@Composable
fun ViewUserScreen(navController: NavController) {

    var personList = remember { mutableStateOf(mutableListOf<Person>()) }
    Scaffold(
        content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.Cyan),
            horizontalAlignment = Alignment.CenterHorizontally) {

                Button(onClick = {
                    navController.navigate(Screen.AddUserScreen.route)
                }, modifier = Modifier
                    .padding(5.dp)) {
                    Text("Return Home")
                }
                Button(onClick = {
                    for (i in 0 until 5) {
                        val person = RandomPersonGenerator().generateRandomPerson()
                        savePerson(person)
                        val tmpList = mutableListOf<Person>()
                        personCollectionRef.get().addOnSuccessListener {
                                collection ->
                            for (document in collection.documents) {
                                println("Iteration.")
                                val person = document.toObject<Person>()
                                println("${person?.firstName} ${person?.lastName} ${person?.age}")

                                if(person != null) tmpList.add(person)
                            }
                            personList.value = tmpList

                        }.addOnFailureListener { e -> println("Failed to retrieve data.")}
                    }
                }, modifier = Modifier
                    .padding(5.dp)) {
                    Text("Add 5 Random People")
                }
                Button(onClick = {
                    personCollectionRef.get().addOnSuccessListener {
                            collection ->
                        for (document in collection.documents) {
                            personCollectionRef.document("${document.id}").delete()
                                .addOnSuccessListener { println("SuccessDelete") }
                                .addOnFailureListener { println("FailureDelete")}
                        }
                        val tmpList = mutableListOf<Person>()
                        personList.value = tmpList
                    }.addOnFailureListener { e -> println("Failed to retrieve data.")}                }, modifier = Modifier
                    .padding(5.dp)) {
                    Text("Wipe Data")

                }
                LazyColumn {
                    items(personList.value.size) {
                        val person = personList.value[it]
                        Card(elevation = 8.dp,
                        backgroundColor = Color.White){
                            Text(text = "${person.firstName} ${person.lastName} ${person.age}",
                            modifier = Modifier.padding(10.dp))
                        }
                            Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                val tmpList = mutableListOf<Person>()
                personCollectionRef.get().addOnSuccessListener {
                        collection ->
                    for (document in collection.documents) {
                        println("Iteration.")
                        val person = document.toObject<Person>()
                        println("${person?.firstName} ${person?.lastName} ${person?.age}")

                        if(person != null) tmpList.add(person)
                    }
                    personList.value = tmpList

                }.addOnFailureListener { e -> println("Failed to retrieve data.")}
            }
        }
    )
}


