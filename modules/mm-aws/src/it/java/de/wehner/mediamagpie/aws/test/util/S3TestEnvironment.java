package de.wehner.mediamagpie.aws.test.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

import de.wehner.mediamagpie.common.util.SearchPathUtil;

public class S3TestEnvironment extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(S3TestEnvironment.class);

    private AWSCredentials _s3Credentials;

    public S3TestEnvironment() {

        InputStream configIS = null;
        try {
            configIS = SearchPathUtil.openStream("classpath:/AwsCredentials.properties");
            _s3Credentials = new PropertiesCredentials(configIS);
            if (StringUtils.isEmpty(_s3Credentials.getAWSAccessKeyId())) {
                LOG.error("No aws access key is specified!");
                _s3Credentials = null;
            } else if (StringUtils.isEmpty(_s3Credentials.getAWSSecretKey())) {
                LOG.error("No aws secret key is specified!");
                _s3Credentials = null;
            }
        } catch (IOException e) {
            LOG.info("Can't load AWS credentials.");
        } finally {
            IOUtils.closeQuietly(configIS);
        }
    }

    public AWSCredentials getS3Credentials() {
        return _s3Credentials;
    }

}
