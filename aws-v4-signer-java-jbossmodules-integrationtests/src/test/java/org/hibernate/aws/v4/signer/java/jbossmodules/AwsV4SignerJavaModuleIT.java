package org.hibernate.aws.v4.signer.java.jbossmodules;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.se.manifest.ManifestDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.lucasweb.aws.v4.signer.HttpRequest;
import uk.co.lucasweb.aws.v4.signer.Signer;
import uk.co.lucasweb.aws.v4.signer.credentials.AwsCredentials;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * AWS-v4-Signer-Java module testing inside Wildfly
 */
@RunWith(Arquillian.class)
public class AwsV4SignerJavaModuleIT {

	private static final VersionsHelper helper = new VersionsHelper();
	private static final String ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
	private static final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

	@Deployment
	public static Archive<?> deployment() {
		String dependencies = "uk.co.lucasweb.aws-v4-signer-java:" + helper.getAwsV4SignerJavaModuleSlot();
		StringAsset manifest = new StringAsset(Descriptors.create(ManifestDescriptor.class)
				.attribute("Dependencies", dependencies)
				.exportAsString());
		return ShrinkWrap.create(WebArchive.class, AwsV4SignerJavaModuleIT.class.getSimpleName() + ".war")
				.addClasses(AwsV4SignerJavaModuleIT.class, VersionsHelper.class)
				.add(manifest, "META-INF/MANIFEST.MF");
	}

	@Test
	public void sign() throws URISyntaxException {
		String hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
		HttpRequest request = new HttpRequest("GET", new URI("https://examplebucket.s3.amazonaws.com?max-keys=2&prefix=J"));

		String signature = Signer.builder()
				.awsCredentials(new AwsCredentials(ACCESS_KEY, SECRET_KEY))
				.header("Host", "examplebucket.s3.amazonaws.com")
				.header("x-amz-date", "20130524T000000Z")
				.header("x-amz-content-sha256", hash)
				.buildS3(request, hash)
				.getSignature();

		String expectedSignature = "AWS4-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20130524/us-east-1/s3/aws4_request, "
				+ "SignedHeaders=host;x-amz-content-sha256;x-amz-date, "
				+ "Signature=34b48302e7b5fa45bde8084f4b7868a86f0a534bc59db6670ed5711ef69dc6f7";

		Assert.assertEquals(expectedSignature, signature);
	}

}
