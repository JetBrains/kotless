package io.kotless.parser.utils.psi.analysis

import org.jetbrains.kotlin.resolve.lazy.ForceResolveUtil

/** Forcefully resolves all contents inside KtElement or Descriptor */
internal fun <T> T.forced(): T = ForceResolveUtil.forceResolveAllContents(this)
