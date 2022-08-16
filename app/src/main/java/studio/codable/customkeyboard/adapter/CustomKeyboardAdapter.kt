package studio.codable.customkeyboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import studio.codable.customkeyboard.CustomKeyboardInputService
import studio.codable.customkeyboard.databinding.CustomKeyboardItemBinding
import studio.codable.customkeyboard.model.Animal

class CustomKeyboardAdapter(
    private val animals: List<Animal> = listOf(),
    private var interactionListener : CustomKeyboardInputService.InteractionListener
) : RecyclerView.Adapter<CustomKeyboardAdapter.CustomKeyboardViewHolder>() {

    inner class CustomKeyboardViewHolder(private val binding: CustomKeyboardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(animal: Animal) {
            binding.apply {
                name.text = animal.name
                Glide.with(image.context)
                    .load(animal.imageUrl)
                    .into(image)
                root.setOnClickListener { interactionListener.onClickItem(animal) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomKeyboardViewHolder {
        val binding =
            CustomKeyboardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomKeyboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomKeyboardViewHolder, position: Int) {
        holder.bind(animals[position])
    }

    override fun getItemCount(): Int {
        return animals.size
    }
}