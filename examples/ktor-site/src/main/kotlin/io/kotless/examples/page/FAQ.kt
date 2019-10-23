package io.kotless.examples.page

import io.kotless.examples.bootstrap.landing
import kotlinx.html.*

object FAQ {
    fun HTML.faq() = landing {
        div("card") {
            div("card-header") {
                +"Why Terraform?"
            }
            div("card-body") {
                +"""We use Terraform as a kind of "cloud deployment bytecode" - it is an intermediate
                        representation of Kotless schema for actual deployment. We've chosen Terraform because
                        it is mature Infrastructure as a Code tool, which is not constrained to one cloud provider."""
                br()
                +"Owing to Terraform Kotless may support Azure or Google Cloud in the future without major changes in architecture."
            }
        }

        br()

        div("card") {
            div("card-header") {
                +"Will you support <cloud-name>?"
            }
            div("card-body") {
                +"""Right now, Kotless supports only AWS deployments. Nevertheless, we consider support
                    of other cloud providers. File issue at project or upvote existing, and we will make
                    our best to support cloud provider you need."""
            }
        }

        br()

        div("card") {
            div("card-header") {
                +"Will you support <aws-functionality-name>?"
            }
            div("card-body") {
                +"""First of all, take a look at our roadmap. Probably, we already consider implementation
                        of functionality you need in later versions."""
                br()
                +"""You don't see it in a roadmap? Don't hesitate to file us issue, but note - Kotless is not aiming to support
                    all possible functionality of AWS or other cloud provider. Our goal is to create simple, but yet powerful
                    interface to cloud. So, if we consider requested functionality excessive, we may reject it."""

            }
        }

        br()

        div("card") {
            div("card-header") {
                +"Why is it so slow/fast/ordinary?"
            }
            div("card-body") {
                +"We are doing all we can to provide the best possible performance of Kotless-based lambdas."
                br()
                +"""Already now, Kotless lambdas are autowarmed. You may also use different generation optimizations
                        to make them even faster."""
                br()
                +"""Nevertheless, well-known problem of cold starts is still here. So, some time you lambdas
                    may take time to bootstrap. In case you suffer from cold starts problem and nothing helps - don't
                    hesitate to file us issue."""

            }
        }
    }
}
