package dev.rohman.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.rohman.myapplication.databinding.FragmentBlankBinding

data class User(val id: Int, val name: String)

class UsersFragment : Fragment() {

    private var binding: FragmentBlankBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlankBinding.inflate(inflater, container, false)

        binding?.run {
            tvTextOne.text = "Text One"
            tvTextTwo.text = "Text Two"

            val users = (1..100).map { User(it, "User $it") }.toList()
            val adapter = UserAdapter(requireContext())
            adapter.list = users

            rvUsers.adapter = adapter
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
    }
}