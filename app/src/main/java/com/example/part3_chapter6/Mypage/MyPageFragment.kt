package com.example.part3_chapter6.Mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.part3_chapter6.R
import com.example.part3_chapter6.databinding.FragmentMyPageBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment(R.layout.fragment_my_page) {

    private var binding: FragmentMyPageBinding?=null

    private val auth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentMyPageBinding = FragmentMyPageBinding.bind(view)
        binding=fragmentMyPageBinding

        fragmentMyPageBinding.signInOutButton.setOnClickListener {
            val email = fragmentMyPageBinding.emailEditText.text.toString()
            val password = fragmentMyPageBinding.passwordEditText.text.toString()

            if (auth.currentUser == null) {
                //로그인
                auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(requireActivity()){ task ->
                        if (task.isSuccessful){
                            successSignIn()
                        } else{Toast.makeText(context,"로그인에 실패했습니다",Toast.LENGTH_SHORT).show()}
                    }

            } else {
                Log.d("이미 user 있음","0")
                auth.signOut()
                fragmentMyPageBinding.emailEditText.text.clear()
                fragmentMyPageBinding.emailEditText.isEnabled = true
                fragmentMyPageBinding.passwordEditText.text.clear()
                fragmentMyPageBinding.passwordEditText.isEnabled = true
                fragmentMyPageBinding.signInOutButton.text="로그인"
                fragmentMyPageBinding.signInOutButton.isEnabled = false
                fragmentMyPageBinding.signUpButton.isEnabled=false
            }

        }
        fragmentMyPageBinding.signUpButton.setOnClickListener {
            val email = fragmentMyPageBinding.emailEditText.text.toString()
            val password = fragmentMyPageBinding.passwordEditText.text.toString()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()){ task ->
                    if (task.isSuccessful){
                        Toast.makeText(context,"회원가입에 성공했습니다",Toast.LENGTH_SHORT).show()
                        auth.signOut()
                        if (auth.currentUser!=null){
                            Log.d("이상발생","0")
                        }
                    }else{
                        Toast.makeText(context,"회원가입에 실패했습니다",Toast.LENGTH_SHORT).show()
                    }

                }
        }

        fragmentMyPageBinding.emailEditText.addTextChangedListener {
            val enable = fragmentMyPageBinding.emailEditText.text.isNotEmpty()
                    && fragmentMyPageBinding.passwordEditText.text.isNotEmpty()
            fragmentMyPageBinding.signInOutButton.isEnabled = enable
            fragmentMyPageBinding.signUpButton.isEnabled = enable
        }
        fragmentMyPageBinding.passwordEditText.addTextChangedListener {
            val enable = fragmentMyPageBinding.emailEditText.text.isNotEmpty()
                    && fragmentMyPageBinding.passwordEditText.text.isNotEmpty()
            fragmentMyPageBinding.signInOutButton.isEnabled = enable
            fragmentMyPageBinding.signUpButton.isEnabled = enable
        }

    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser==null){
            binding?.let { binding ->
                binding.emailEditText.text.clear()
                binding.passwordEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.isEnabled = true
                binding.signInOutButton.text ="로그인"
                binding.signInOutButton.isEnabled = false
                binding.signUpButton.isEnabled =false
            }
        } else{
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser?.email)
                binding.passwordEditText.setText("********")
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false
                binding.signInOutButton.text ="로그아웃"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled =false
            }

        }
    }


    private fun successSignIn() {
        if (auth.currentUser==null){
            Toast.makeText(context,"로그인에 실패했습니다.",Toast.LENGTH_SHORT).show()
        }
        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpButton?.isEnabled = false
        binding?.signInOutButton?.text = "로그아웃"
        Toast.makeText(context,"로그인에 성공했습니다.",Toast.LENGTH_SHORT).show()


    }
}

