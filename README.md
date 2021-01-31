# Bye bye findViewById and Kotlin Synthetics

## findViewById

findViewById merupakan cara yang lama untuk mendapatkan view dari layout dengan memasukkan tipe view dan id viewnya.

```xml
<TextView
   android:id="@+id/tv_text"
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"
   android:text="Hello World!" />
```

penggunaan findViewById pada Kotlin

```kotlin
val tvText = findViewById<TextView>(R.id.tv_text)
tvText.text = "Hello guys"
```

## Kotlin Synthetics

Kotlin synthetics merupakan fitur yang hanya tersedia di kotlin dengan tujuan untuk memanggil view secara langsung dari layout  dengan memanggil idnya saja. 
Untuk bisa memakai kotlin synthetics, perlu menambahkan plugin pada `build.gradle` app:

```groovy
apply plugin: 'kotlin-android-extensions'
```

__atau__

```groovy
id 'kotlin-android-extensions'
```

Cara penggunaan Kotlin Synthetics

```xml
<TextView
   android:id="@+id/tv_text"
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"
   android:text="Hello World!" />
```

penggunaan Kotlin Synthetics pada Kotlin

```kotlin
tv_text.text = "Hello guys"
```

## Alasan meninggalkan findViewById and Kotlin synthetics

Beberapa alasan mengapa harus meninggalkan findViewById dan Kotlin synthetics:
1. Null Safety.
2. Type Safety.
3. Bad convention name.
4. Deprecated.

### Null safety

Karena proses binding view findViewById dan Kotlin synthetic pada Android berdasarkan id, maka menjadi masalah ketika id yang dimasukkan tidak ada pada layout. 

### Type Safety

Proses view binding pada findViewById harus memberikan tipe view yang akan dimuat, masalahnya type view yang akan dimuat sama dengan yang ada di layout tidak.

### Bad Convention Name

Proses view binding menggunakan Kotlin synthetic akan memanggil id secara langsung di layout, masalahnya adalah nama id di layout biasanya berbentuk snake_case padahal di dalam Kotlin nama variabel harusnya menggunakan kebabCase.

### Deprecated

Kotlin synthetic sudah deprecated sejak releasenya [kotlin versi 1.4.20](https://blog.jetbrains.com/kotlin/2020/11/kotlin-1-4-20-released/)

### Solusi dari findViewById dan Kotlin synthetic

Adakah solusi yang dapat mengatasi dari tiga kekurangan sebelumnya? Ada yaitu: [Android Jetpack View Binding](https://developer.android.com/topic/libraries/view-binding)

## Apa itu Android Jetpack View Binding

View Binding adalah salah satu fitur dari Android Jetpack yang memungkinkan untuk mengikat view dari layout secara mudah dan aman.

### Setup View Binding

Dalam memakai viewBinding tidak perlu menambahkan package pada gradle.

```groovy
buildFeatures {
   viewBinding true
}
```

### Penggunaan View Binding di Activity

Penggunaan View Binding di Activity cukup simple pertama buat variable binding yang didapatkan dari ActivityNameBinding.inflate(). yang menerima argument layoutInflater dari Activity.

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/tv_message"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="8dp"
        android:gravity="center" />

    <FrameLayout
        android:id="@+id/fl_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</LinearLayout>
```

penggunaa view binding di Activity

```kotlin
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tvMessage.text = "Hello guys"

        supportFragmentManager.beginTransaction().replace(R.id.fl_main, UsersFragment()).commit()
    }
}
```

### Penggunaan View Binding di Fragment

Penggunaan View Binding di Fragment cukup berbeda dengan Activity, seperti biasa buat variable binding FragmentNameBinding.inflate().

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".UsersFragment">

    <TextView
        android:id="@+id/tv_text_one"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center" />

    <TextView
        android:id="@+id/tv_text_two"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_users"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />

</LinearLayout>
```

penggunaan view binding di Fragment

```kotlin
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
```

### Penggunaan View Binding di Adapter

Penggunaan View Binding di Adapter cukup berbeda juga dengan Activity dan Fragment, viewBinding akan dipassing pada parameter constructor dari viewHolder.

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TextView
        android:id="@+id/tv_name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="8dp"
        app:layout_constraintTop_toTopOf="parent" />

</LinearLayout>
```

penggunaan view binding di Adapter

```kotlin
class UserAdapter(val context: Context) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(user: User) {
            binding.tvName.text = user.name
        }
    }

    var list: List<User> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size
}
```