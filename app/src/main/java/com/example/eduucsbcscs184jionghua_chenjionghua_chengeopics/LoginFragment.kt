package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this)[LoginViewModel::class.java]
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.controller = this
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Authentification"
    }

    fun signUp(view: View) {
        val username = binding.edittextUsername.text.toString()
        val password = binding.edittextPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(),"Invalid username or password!",Toast.LENGTH_SHORT)
                .show()
        } else {
            mAuth
                .createUserWithEmailAndPassword(username,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(),"Registration successful!",Toast.LENGTH_SHORT)
                            .show()
                        Thread.sleep(2_000)
                        logIn(view)
                    } else {
                        Log.d("registration",task.exception?.message ?: "r")
                        Toast.makeText(requireContext(),"Registration failed!\nPlease try again later.",Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }

    }

    fun logIn(view: View) {
        val username = binding.edittextUsername.text.toString()
        val password = binding.edittextPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(),"Invalid username or password!",Toast.LENGTH_SHORT)
                .show()
        } else {
            mAuth
                .signInWithEmailAndPassword(username,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(),"Login successful!",Toast.LENGTH_SHORT)
                            .show()
                        Thread.sleep(2_000)
                        findNavController().navigate(R.id.action_loginFragment_to_googleMapFragment)

                    } else {
                        Toast.makeText(requireContext(),"Login failed!\nPlease try again later.",Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }


    }

}