package org.endeavourhealth.enterprise.core;

import com.jcraft.jsch.*;
import org.slf4j.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

public class FtpWrapper implements AutoCloseable {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FtpWrapper.class);
    private final ChannelSftp sftpChannel;
    private final Session session;

    public FtpWrapper(FtpConnectionDetails connectionDetails) throws JSchException {

        JSch jsch = new JSch();
        session = jsch.getSession( connectionDetails.getUsername(), connectionDetails.getHostname());
        {
            //This will allow access to any site without putting the key in hosts store
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(connectionDetails.getPassword());
        }

        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();

        sftpChannel = (ChannelSftp) channel;
    }

    @Override
    public void close() throws Exception {
        sftpChannel.exit();
        session.disconnect();
    }

    public void createFolder(Path path) throws SftpException {
        sftpChannel.mkdir(path.toString());
    }

    public static void createFolder(FtpConnectionDetails ftpConnectionDetails, Path path) throws Exception {
        try (FtpWrapper wrapper = new FtpWrapper(ftpConnectionDetails)) {
            wrapper.createFolder(path);
        }
    }

    public void writeFileToDisk(Path path, String content) throws SftpException {
        logger.trace("Writing file to ftp: " + path.toString());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());

        sftpChannel.put(inputStream, path.toString(), ChannelSftp.OVERWRITE);
    }

    public static class FtpConnectionDetails {
        private final String hostname;
        private final String username;
        private final String password;

        public FtpConnectionDetails(String hostname, String username, String password) {
            this.hostname = hostname;
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getHostname() {
            return hostname;
        }

        public String getPassword() {
            return password;
        }
    }
}
