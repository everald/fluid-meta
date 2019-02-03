package com.github.fluidsonic.fluid.meta

import com.github.fluidsonic.fluid.stdlib.*
import kotlinx.metadata.ClassName
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClassVisitor
import kotlinx.metadata.KmExtensionType
import kotlinx.metadata.KmVariance
import kotlinx.metadata.jvm.JvmClassExtensionVisitor


internal class MClassBuilder : KmClassVisitor() {

	private var anonymousObjectOriginName: MQualifiedTypeName? = null
	private var companion: MQualifiedTypeName? = null
	private var constructors: MutableList<MConstructorBuilder>? = null
	private var enumEntries: MutableList<MEnumEntry>? = null
	private var flags: Flags = 0
	private var functions: MutableList<MFunctionBuilder>? = null
	private var localDelegatedProperties: MutableList<MPropertyBuilder>? = null
	private var name: MQualifiedTypeName? = null
	private var nestedClasses: MutableList<MQualifiedTypeName>? = null
	private var properties: MutableList<MPropertyBuilder>? = null
	private var sealedSubclasses: MutableList<MQualifiedTypeName>? = null
	private var supertypes: MutableList<MTypeReferenceBuilder>? = null
	private var typeAliases: MutableList<MTypeAliasBuilder>? = null
	private var typeParameters: MutableList<MTypeParameterBuilder>? = null
	private var versionRequirement: MVersionRequirementBuilder? = null


	fun build() = MClass(
		anonymousObjectOriginName = anonymousObjectOriginName,
		companion = companion,
		constructors = constructors.mapOrEmpty { it.build() },
		enumEntries = enumEntries.toListOrEmpty(),
		flags = flags,
		functions = functions.mapOrEmpty { it.build() },
		name = name,
		localDelegatedProperties = localDelegatedProperties.mapOrEmpty { it.build() },
		nestedClasses = nestedClasses.toListOrEmpty(),
		properties = properties.mapOrEmpty { it.build() },
		sealedSubclasses = sealedSubclasses.toListOrEmpty(),
		supertypes = supertypes.mapOrEmpty { it.build() },
		typeAliases = typeAliases.mapOrEmpty { it.build() },
		typeParameters = typeParameters.mapOrEmpty { it.build() },
		versionRequirement = versionRequirement?.build()
	)


	override fun visit(flags: Flags, name: ClassName) {
		this.flags = flags
		this.name = MQualifiedTypeName.fromKotlinInternal(name)
	}


	override fun visitCompanionObject(name: ClassName) {
		companion = MQualifiedTypeName.fromKotlinInternal(name)
	}


	override fun visitConstructor(flags: Flags) =
		MConstructorBuilder(flags = flags)
			.also {
				constructors?.apply { add(it) } ?: { constructors = mutableListOf(it) }()
			}


	override fun visitEnumEntry(name: String) {
		enumEntries?.apply { add(MEnumEntry(name)) } ?: { enumEntries = mutableListOf(MEnumEntry(name)) }()
	}


	override fun visitExtensions(type: KmExtensionType) =
		(type == JvmClassExtensionVisitor.TYPE).thenTake {
			object : JvmClassExtensionVisitor() {

				override fun visitAnonymousObjectOriginName(internalName: ClassName) {
					anonymousObjectOriginName = MQualifiedTypeName.fromKotlinInternal(internalName)
				}


				override fun visitLocalDelegatedProperty(flags: Flags, name: String, getterFlags: Flags, setterFlags: Flags) =
					MPropertyBuilder(flags = flags, getterFlags = getterFlags, name = MVariableName(name), setterFlags = setterFlags)
						.also {
							localDelegatedProperties?.apply { add(it) }
								?: { localDelegatedProperties = mutableListOf(it) }()
						}
			}
		}


	override fun visitFunction(flags: Flags, name: String) =
		MFunctionBuilder(flags = flags, name = MFunctionName(name))
			.also {
				functions?.apply { add(it) } ?: { functions = mutableListOf(it) }()
			}


	override fun visitNestedClass(name: ClassName) {
		nestedClasses?.apply { add(MQualifiedTypeName.fromKotlinInternal(name)) }
			?: { nestedClasses = mutableListOf(MQualifiedTypeName.fromKotlinInternal(name)) }()
	}


	override fun visitProperty(flags: Flags, name: String, getterFlags: Flags, setterFlags: Flags) =
		MPropertyBuilder(flags = flags, getterFlags = getterFlags, name = MVariableName(name), setterFlags = setterFlags)
			.also {
				properties?.apply { add(it) } ?: { properties = mutableListOf(it) }()
			}


	override fun visitSealedSubclass(name: ClassName) {
		sealedSubclasses?.apply { add(MQualifiedTypeName.fromKotlinInternal(name)) }
			?: { sealedSubclasses = mutableListOf(MQualifiedTypeName.fromKotlinInternal(name)) }()
	}


	override fun visitSupertype(flags: Flags) =
		MTypeReferenceBuilder(flags = flags)
			.also {
				supertypes?.apply { add(it) } ?: { supertypes = mutableListOf(it) }()
			}


	override fun visitTypeAlias(flags: Flags, name: String) =
		MTypeAliasBuilder(flags = flags, name = name)
			.also {
				typeAliases?.apply { add(it) } ?: { typeAliases = mutableListOf(it) }()
			}


	override fun visitTypeParameter(flags: Flags, name: String, id: Int, variance: KmVariance) =
		MTypeParameterBuilder(flags = flags, id = MTypeParameterId(id), name = MTypeParameterName(name), variance = MVariance(variance))
			.also {
				typeParameters?.apply { add(it) } ?: { typeParameters = mutableListOf(it) }()
			}


	override fun visitVersionRequirement() =
		MVersionRequirementBuilder()
			.also { versionRequirement = it }
}
