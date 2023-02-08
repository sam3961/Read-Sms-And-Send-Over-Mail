package com.app.readsms

import android.util.Log
import com.app.readsms.JSSEProvider
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.security.Security
import java.util.*
import javax.activation.DataSource
import javax.mail.*
import kotlin.jvm.Synchronized
import kotlin.Throws
import javax.mail.internet.MimeMessage
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimeBodyPart

/**
 * Created by Vishal on 6/20/2017.
 */
class GMailSender(private val user: String, private val password: String) : Authenticator() {
    private val mailhost = "smtp.gmail.com"
    private val session: Session

    companion object {
        init {
            Security.addProvider(JSSEProvider())
        }
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, password)
    }

    @Synchronized
    @Throws(Exception::class)
    fun sendMail(
        subject: String?, body: String?, sender: String?,
        recipients: String
    ) {
        try {
            val message = MimeMessage(session)
            message.sender = InternetAddress(sender)
            message.subject = subject
            message.setText(body)


            if (recipients.indexOf(',') > 0) message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recipients)
            ) else message.setRecipient(
                Message.RecipientType.TO, InternetAddress(recipients)
            )
            val t = session.getTransport("smtp")
            t.connect(user, password)
            t.sendMessage(message, message.allRecipients)
            t.close()
            //    Transport.send(message);
        } catch (e: Exception) {
            Log.d("mylog", "Error in sending: $e")
        }
    }


    inner class ByteArrayDataSource : DataSource {
        private var data: ByteArray
        private var type: String? = null

        constructor(data: ByteArray, type: String?) : super() {
            this.data = data
            this.type = type
        }

        constructor(data: ByteArray) : super() {
            this.data = data
        }

        fun setType(type: String?) {
            this.type = type
        }

        override fun getContentType(): String {
            return if (type == null) "application/octet-stream" else type!!
        }

        @Throws(IOException::class)
        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(data)
        }

        override fun getName(): String {
            return "ByteArrayDataSource"
        }

        @Throws(IOException::class)
        override fun getOutputStream(): OutputStream {
            throw IOException("Not Supported")
        }
    }

    init {
        val props = Properties()
        props.setProperty("mail.transport.protocol", "smtp")
        props["mail.smtp.user"] = user
        props.setProperty("mail.smtp.host", mailhost)
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        //props["mail.smtp.ssl.trust"] = mailhost


        props["mail.smtp.port"] = "587"
        // props.put("mail.smtp.socketFactory.port", "587");
        // props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //      props.put("mail.smtp.socketFactory.fallback", "false");
//        props.setProperty("mail.smtp.quitwait", "false");

        //session = Session.getDefaultInstance(props, this);
        //  Session session = Session.getInstance(props, new GMailAuthenticator(user, password));
        //session.setDebug(true);
        session = Session.getInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(user, password)
                }
            })
    }
}