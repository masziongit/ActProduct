package connect;

import com.jcraft.jsch.*;
import org.apache.log4j.Logger;

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

                    case 'U':
                        sftpChannel.put(fileName, prop.getProperty("sftp.upload.path")+fileName);
                        logger.info("UploadFile from " + sftpChannel.lpwd() + " to "
                                + prop.getProperty("sftp.upload.path") + " Complete!");
                        break;

                    case 'D':
                        sftpChannel.get(prop.getProperty("sftp.download.path")+fileName,fileName);
                        logger.info("DownloadFile from " + prop.getProperty("sftp.download.path") + " to "
                                + sftpChannel.lpwd() + " Complete!");

                        sftpChannel.exit();
                }

                sftpChannel.exit();

            } catch (JSchException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            } catch (SftpException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
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