package connect;

import com.jcraft.jsch.*;
import org.apache.log4j.Logger;
import util.Constant;

import java.util.Properties;

/**
 * @author javagists.com
 */
public class FileSFTP {

    final static org.apache.log4j.Logger logger = Logger.getLogger(FileSFTP.class);


    public FileSFTP(Properties prop, char mode,String fileName) {
        {
            Session session = null;
            try {

                session = getSession(prop);
                ChannelSftp sftpChannel = getchanel(session);
                switch (mode) {

                    case Constant.Mode.UPLOAD :
                        sftpChannel.put(fileName, prop.getProperty("sftp.upload.path")+fileName);
                        logger.info("UploadFile  to " + prop.getProperty("sftp.upload.path") + " Complete!");
                        break;

                    case Constant.Mode.DOWNLOAD:
                        sftpChannel.get(prop.getProperty("sftp.download.path")+fileName,fileName);
                        logger.info("DownloadFile from " + prop.getProperty("sftp.download.path")+" Complete!");
                }

                sftpChannel.exit();

            } catch (JSchException e) {
                logger.error(e);
            } catch (SftpException e) {
                logger.error(e);
            } finally {
                logger.info("Disconnect from SFTP : " + session.getHost());
                session.disconnect();
            }

        }
    }

    private Session getSession(Properties prop) throws JSchException {

        JSch jsch = new JSch();

        logger.info("Start Connection to SFTP");

        Session session = jsch.getSession(prop.getProperty("sftp.user")
                , prop.getProperty("sftp.host"), Integer.valueOf(prop.getProperty("sftp.port")));
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(prop.getProperty("sftp.password"));
        session.connect();

        logger.info("Connection to SFTP host " + session.getHost());

        return session;
    }

    private ChannelSftp getchanel(Session session) throws JSchException {

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        return sftpChannel;
    }

}