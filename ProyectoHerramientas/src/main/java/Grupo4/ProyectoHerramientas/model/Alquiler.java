package Grupo4.ProyectoHerramientas.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "alquileres")
public class Alquiler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // ... (Mantén tus anotaciones e imports de LocalDate anteriores)

    @Column(name = "fecha_contrato")
    private LocalDate fechaContrato;

    @Column(name = "fecha_recojo")
    private LocalDate fechaRecojo; // <-- NUEVA COLUMNA

    @Column(name = "dias_alquiler", nullable = false)
    private Integer diasAlquiler;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Column(name = "monto_total")
    private BigDecimal montoTotal;

    @Column(nullable = false, length = 20)
    private String estado;

    @OneToMany(mappedBy = "alquiler", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();

    @Column(name = "estado_operativo", nullable = false)
    private String estadoOperativo; // "NO_DEVUELTO", "DEVUELTO", "ANULADO"

    @Column(columnDefinition = "TEXT")
    private String observacion; // Para motivos de anulación, notas de entrega, etc.

    @PrePersist
    protected void onCreate() {
        // 1. La fecha del contrato siempre es HOY (cuando presiona el botón)
        if (this.fechaContrato == null) {
            this.fechaContrato = LocalDate.now();
        }

        // 2. El recojo siempre es AL DÍA SIGUIENTE del contrato
        this.fechaRecojo = this.fechaContrato.plusDays(1);

        if (this.vehiculo != null && this.diasAlquiler != null) {
            // 3. La devolución se calcula sumando los días a la fecha de RECOJO (no a la
            // del contrato)
            this.fechaDevolucion = this.fechaRecojo.plusDays(this.diasAlquiler);

            // 4. El monto total sigue siendo el precio por día por la cantidad de días
            this.montoTotal = this.vehiculo.getPrecioPorDia().multiply(BigDecimal.valueOf(this.diasAlquiler));
        }

        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }

        this.estadoOperativo = "NO_DEVUELTO";
    }

    @Transient
    public BigDecimal getDeuda() {
        // 1. En BigDecimal no podemos usar '0.0', usamos la constante BigDecimal.ZERO
        if (this.estadoOperativo != null && this.estadoOperativo.equals("ANULADO")) {
            return BigDecimal.ZERO;
        }

        // Si no hay pago registrado, la deuda es el monto total completo
        if (this.pagos == null || this.pagos.isEmpty()) {
            return this.montoTotal != null ? this.montoTotal : BigDecimal.ZERO;
        }

        // 2. Acumulamos el total abonado usando BigDecimal.ZERO como punto de partida
        BigDecimal totalAbonado = BigDecimal.ZERO;
        for (Pago p : this.pagos) {
            if (p.getMonto() != null) {
                // En lugar de += usamos .add()
                totalAbonado = totalAbonado.add(p.getMonto());
                // NOTA: Si p.getMonto() ya fuera un BigDecimal, usa: totalAbonado =
                // totalAbonado.add(p.getMonto());
            }
        }

        // 3. Si por alguna razón montoTotal es nulo, lo protegemos para evitar
        // NullPointerException
        BigDecimal total = this.montoTotal != null ? this.montoTotal : BigDecimal.ZERO;

        // 4. En lugar de el operador menos (-), usamos .subtract()
        BigDecimal calculo = total.subtract(totalAbonado);

        // 5. En lugar de '>' usamos .compareTo().
        // calculo.compareTo(BigDecimal.ZERO) > 0 significa "si calculo es mayor que
        // cero"
        return calculo.compareTo(BigDecimal.ZERO) > 0 ? calculo : BigDecimal.ZERO;
    }

    public String getEstadoOperativo() {
        return estadoOperativo;
    }

    public void setEstadoOperativo(String estadoOperativo) {
        this.estadoOperativo = estadoOperativo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public LocalDate getFechaRecojo() {
        return fechaRecojo;
    }

    public void setFechaRecojo(LocalDate fechaRecojo) {
        this.fechaRecojo = fechaRecojo;
    }

    public Integer getDiasAlquiler() {
        return diasAlquiler;
    }

    public void setDiasAlquiler(Integer diasAlquiler) {
        this.diasAlquiler = diasAlquiler;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFechaContrato() {
        return fechaContrato;
    }

    public void setFechaContrato(LocalDate fechaContrato) {
        this.fechaContrato = fechaContrato;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}