package org.hibernate.aws.v4.signer.java.jbossmodules;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class VersionsHelper {

	public String getAwsV4SignerJavaModuleSlot() {
		return getPropertiesVariable("aws-v4-signer-java.slot.id");
	}

	private static String getPropertiesVariable(String key) {
		Properties projectCompilationProperties = new Properties();
		final InputStream resourceAsStream = VersionsHelper.class.getClassLoader()
				.getResourceAsStream("module-versions.properties");
		try {
			projectCompilationProperties.load(resourceAsStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				resourceAsStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return projectCompilationProperties.getProperty(key);
	}

}
