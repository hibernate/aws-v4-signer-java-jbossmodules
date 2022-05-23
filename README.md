**This repository is no longer maintained.**

Newer versions of Hibernate projects use the AWS SDK instead of AWS v4 Signer for Java.

AWS v4 Signer for Java JBoss Module as WildFly feature pack
============================================================

This creates a set of [JBoss Module](https://jboss-modules.github.io/jboss-modules/manual/)s containing [AWS v4 Signer for Java](https://github.com/lucasweb78/aws-v4-signer-java)
and packages the libraries into a WildFly *feature pack* for easy provisioning into your custom [WildFly](http://wildfly.org/) server or [WildFly Swarm](http://wildfly-swarm.io/).

Historically the Hibernate Search project has been releasing such modules,
for convenience of Hibernate Search users running applications on WildFly or JBoss EAP.

We now moved these artifacts to a dedicated bundle so that other projects using the AWS v4 Signer
don't have to download the Hibernate Search specific modules,
and to attempt to have a single consistent distribution of such modules.

This should also make it easier to release a new bundle of these modules
as soon as a new AWS v4 Signer version is released,
without necessarily waiting for Hibernate Search to have adopted the new version.

## Version conventions

We will use an `X.Y.Z.qualifier` pattern as recommended by
[JBoss Project Versioning](https://developer.jboss.org/wiki/JBossProjectVersioning),
wherein the `X.Y.Z` section will match the version of the AWS v4 Signer version included in the modules,
possibly using a `0` for the last figure when this is missing in the AWS v4 Signer version scheme.

We will add an additional qualifier, lexicographically increasing with further releases, to distinguish
different releases of this package when still containing the same version of AWS v4 Signer library.
This might be useful to address problems in the package structure, or any other reason to have
to release a new version of these modules containing the same AWS v4 Signer version as a previously
released copy of these modules.

An example version could be `1.3.0.hibernate02` to contain AWS v4 Signer version `1.3`.

## Usage: server provisioning via Maven

Maven users can use the `wildfly-server-provisioning-maven-plugin` to create a custom WildFly server including these modules:

	<plugins>
		<plugin>
		<groupId>org.wildfly.build</groupId>
		<artifactId>wildfly-server-provisioning-maven-plugin</artifactId>
		<version>1.2.0.Final</version>
		<executions>
			<execution>
			<id>server-provisioning</id>
			<goals>
				<goal>build</goal>
			</goals>
			<phase>compile</phase>
			<configuration>
				<config-file>server-provisioning.xml</config-file>
				<server-name>minimal-wildfly-with-aws-v4-signer-java</server-name>
			</configuration>
			</execution>
		</executions>
	</plugin>

You will also need a `server-provisioning.xml` in the root of your project:

	<server-provisioning xmlns="urn:wildfly:server-provisioning:1.1">
		<feature-packs>
	
			<feature-pack
				groupId="org.hibernate.aws-v4-signer-java-jbossmodules"
				artifactId="aws-v4-signer-java-jbossmodules"
				version="${aws-v4-signer-java-modules.version}"/>
	
			<feature-pack
				groupId="org.wildfly"
				artifactId="wildfly-servlet-feature-pack"
				version="${your-preferred-wildfly.version}" />
	
		</feature-packs>
	</server-provisioning>

This will make AWS-v4-signer-java available as an opt-in dependency to any application deployed on WildFly.
To enable the dependency there are various options, documented in [Class Loading in WildFly](https://docs.jboss.org/author/display/WFLY/Class+Loading+in+WildFly).

N.B.:

* The current version of these modules has been tested with `WildFly 11.0.0.Final`.
* Depending on the WildFly feature pack you chose, some transitive dependencies may not be available in Maven Central.
  In that case, you should [set up the JBoss Nexus repository](https://developer.jboss.org/wiki/MavenGettingStarted-Users).

## Non-Maven users

Plugins for other build tools have not been implemented yet, but this should be quite straight forward to do: the above Maven plugin is just a thin wrapper invoking other libraries; these other libraries are build agnostic and are responsible for performing most of the work.

See also [WildFly provisioning build tools](https://github.com/wildfly/wildfly-build-tools).

The feature packs are also available for downloads as zip files on [JBoss Nexus](https://repository.jboss.org/nexus/index.html#welcome).

## How to build

Use the provided settings-example.xml file, so that JBoss-specific dependencies can be found:

    mvn -s settings-example.xml clean install

## How to release

    mvn -s settings-example.xml release:prepare
    mvn -s settings-example.xml release:perform

This will produce two local commits and a local tag, then upload the artifacts to a staging repository on [JBoss Nexus](https://repository.jboss.org/nexus/index.html#welcome).

If it all works fine, don't forget to:

 * release the staging repository on Nexus
 * push the commits
 * push the tag

