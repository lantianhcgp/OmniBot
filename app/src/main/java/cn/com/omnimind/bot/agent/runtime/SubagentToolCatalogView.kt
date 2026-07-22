package cn.com.omnimind.bot.agent

import kotlinx.serialization.json.JsonObject

/**
 * A filtered view over an existing [AgentToolCatalog] that only exposes
 * tools allowed by the active [SubagentProfile].
 *
 * When [allowTerminal] is true, `terminal_execute` is added to the
 * effective tool set in addition to the declared [allowed] tools.
 *
 * Any attempt to access a tool outside the effective whitelist throws
 * [IllegalStateException], preventing a subagent from escalating beyond
 * its declared scope.
 */
class SubagentToolCatalogView(
    private val parent: AgentToolCatalog,
    private val allowed: Set<String>,
    private val allowTerminal: Boolean = false
) : AgentToolCatalog {

    private val effectiveAllowed: Set<String> by lazy {
        if (allowTerminal) allowed + "terminal_execute" else allowed
    }

    override val toolsForModel: List<ChatCompletionTool> by lazy {
        parent.toolsForModel.filter { tool ->
            tool.function.name in effectiveAllowed
        }
    }

    override fun runtimeDescriptor(toolName: String): AgentToolRegistry.RuntimeToolDescriptor {
        ensureAllowed(toolName)
        return parent.runtimeDescriptor(toolName)
    }

    override fun validateArguments(toolName: String, arguments: JsonObject) {
        ensureAllowed(toolName)
        parent.validateArguments(toolName, arguments)
    }

    private fun ensureAllowed(toolName: String) {
        if (toolName !in effectiveAllowed) {
            throw IllegalStateException(
                "tool '$toolName' is not allowed for this subagent (whitelist=${effectiveAllowed.size})"
            )
        }
    }
}
