package com.github.fluidsonic.fluid.meta

@Suppress("EqualsOrHashCode")
data class MFunction(
	val contract: MContract?,
	override val inheritanceRestriction: MInheritanceRestriction,
	override val isExpect: Boolean,
	override val isExternal: Boolean,
	val isInfix: Boolean,
	override val isInline: Boolean,
	val isOperator: Boolean,
	val isSuspend: Boolean,
	val isTailrec: Boolean,
	override val jvmSignature: MJvmMemberSignature.Method?,
	val lambdaClassOriginName: MQualifiedTypeName?,
	val name: MFunctionName,
	override val receiverParameterType: MTypeReference?,
	val returnType: MTypeReference,
	override val source: MClassMemberSource,
	override val typeParameters: List<MTypeParameter>,
	override val valueParameters: List<MValueParameter>,
	override val versionRequirements: List<MVersionRequirement>,
	override val visibility: MVisibility
) : MClassMember,
	MExecutable,
	MExpectable,
	MExternalizable,
	MGeneralizable,
	MIdentifyable,
	MInlineable,
	MInheritanceRestrictable,
	MReceiverDeclarable,
	MVersionRestrictable,
	MVisibilityRestrictable {

	override val localId by lazy {
		MLocalId.Function(
			name = name,
			receiverParameterType = receiverParameterType,
			valueParameters = valueParameters
		)
	}


	override fun hashCode() =
		localId.hashCode()


	override fun toString() =
		MetaCodeWriter.write(this)


	companion object
}
