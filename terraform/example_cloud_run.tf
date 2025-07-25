resource "google_cloud_run_v2_service" "tfc_cloud_run_service" {
  name     = "tfc-cloud-run-service"
  location = "europe-west1"

  template {
    containers {
      image = "gcr.io/cloudrun/backend-image"
    }
    service_account = var.workload_sa_email
  }
}

output "tfc_cloud_run_service_url" {
  value = google_cloud_run_v2_service.tfc_cloud_run_service.uri
}
