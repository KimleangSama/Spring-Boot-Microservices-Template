```shell
istioctl install -f k8s/istio/istiooperator.yml
```

This above command installs Istio using the configuration specified in the `istiooperator.yml` file located in the
`k8s/istio`
directory.

```shell
kubectl label namespace default istio-injection=enabled
```

This above command enables automatic sidecar injection for the `default` namespace, allowing Istio to manage traffic
within
that namespace.

```shell
kubectl get crd gateways.gateway.networking.k8s.io &> /dev/null || \
  kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.3.0/standard-install.yaml
```

This above command checks if the `gateways.gateway.networking.k8s.io` Custom Resource Definition (CRD) exists. If it does not,
it applies the standard installation of the Gateway API from the specified URL. This is useful for managing ingress
traffic in Kubernetes clusters. Run it before deploying the Istio gateway.
