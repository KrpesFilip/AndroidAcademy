import com.example.academyproject.util.AppLogger
import com.example.academyproject.util.Logger

class FakeLogger : Logger {

    override fun logD(message: String) {}
    override fun logE(message: String) {}
    override fun logI(message: String) {}
    override fun logW(message: String) {}
}