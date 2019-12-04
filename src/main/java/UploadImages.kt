import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.Acl
import com.google.cloud.storage.Acl.Role
import com.google.cloud.storage.Acl.User
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.StorageOptions
import com.google.gson.Gson
import java.io.FileInputStream
import java.nio.file.Paths


data class Upload(val name: String, val link: String)
data class UploadResult(val uploads: MutableList<Upload> = mutableListOf()){
    fun contains(name: String) = uploads.any { it.name == name }
}

fun main() {
    // Download file json key trên firebase xuống máy rồi sửa lại đường dẫn tới file
    // Sửa lại bucketname
    // Tạo một thư mục images trên firebase storage

    //Firebase service account
    //firebase-adminsdk-4hyiq@drivertest-385f6.iam.gserviceaccount.com
    val keyJson = "/Users/admin/Dropbox/Dev/drivertest.json"
    val bucketName = "drivertest-385f6.appspot.com"

    val options =  StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream(keyJson)))
            .build()

    val storage = options.service

    val uploadResultPath = Paths.get("download", "uploaded.json").toString()

    if(!uploadResultPath.fileExist()){
        UploadResult().saveTo(uploadResultPath)
    }

    val result = Gson().fromJson(uploadResultPath.readFile(), UploadResult::class.java) as UploadResult

    val images = Paths.get("download", "images").toFile().list()!!

    images.forEach {image ->
        if(!result.contains(image)){
            val fullPath = Paths.get("download", "images", image).toString()

            val bytes = fullPath.readFileAsBytes()
            val blobInfo = BlobInfo
                    .newBuilder(bucketName, "images/$image")
                    .setAcl(listOf(Acl.of(User.ofAllUsers(), Role.READER)))
                    .build()

            val blob = storage.create(blobInfo, bytes)

            result.uploads.add(Upload(image, blob.mediaLink))

            println("${result.uploads.size}/${images.size} Uploaded $image -> ${blob.mediaLink}")

            result.saveTo(uploadResultPath)
        }
    }

}