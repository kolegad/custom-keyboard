package studio.codable.customkeyboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import studio.codable.customkeyboard.databinding.ItemCustomKeyboardBinding
import studio.codable.customkeyboard.model.Animal

class CustomKeyboardAdapter(
    private val animals: List<Animal> = listOf(),
    private val onClickItem: (Animal) -> Unit
) : RecyclerView.Adapter<CustomKeyboardAdapter.CustomKeyboardViewHolder>() {

    inner class CustomKeyboardViewHolder(private val binding: ItemCustomKeyboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var animal: Animal? = null

        private val onClickListener = View.OnClickListener {
            animal?.let(onClickItem)
        }

        fun bind(bindAnimal: Animal) {
            animal = bindAnimal
            animal?.let {
                binding.apply {
                    name.text = it.name
                    Glide.with(image.context)
                        .load(it.imageUrl)
                        .into(image)
                    root.setOnClickListener(onClickListener)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomKeyboardViewHolder {
        val binding =
            ItemCustomKeyboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomKeyboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomKeyboardViewHolder, position: Int) {
        holder.bind(animals[position])
    }

    override fun getItemCount(): Int {
        return animals.size
    }
}