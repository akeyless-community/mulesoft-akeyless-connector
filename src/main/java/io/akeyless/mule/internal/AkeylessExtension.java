package io.akeyless.mule.internal;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

/**
 * Entry point for the Akeyless Mule 4 connector.
 */
@Xml(prefix = "akeyless")
@Extension(name = "Akeyless")
@Operations(AkeylessOperations.class)
@ConnectionProviders(AkeylessConnectionProvider.class)
public class AkeylessExtension {
}
