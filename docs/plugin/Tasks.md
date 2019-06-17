## Tasks

Kotless have two types of tasks -- service and end-user. In spite of fact that service
tasks are also available for end-user, you should not call them explicitly. All of them will
be called as dependencies of end-user tasks.

Note, that 99% percent of the time you should execute just `./gradlew deploy`

End-user tasks:
* `deploy` - task that actually deploy your Kotless-based application to cloud provider.
   Task will call all other needed tasks by dependencies, and most of the time you should use
   only this task.
* `plan` - task that "plans" the deployment. The result of this task is a log of changes
   that will be applied to cloud provider (terraform generated)

Service tasks:
* `generate` - task that generates deployment definition (terraform code) for your application
* `init` - task that performs `terraform init` on generated terraform code
* `download_terraform` - task that downloads required version of terraform from HashiCorp site

