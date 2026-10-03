package dev.kineticcat.complexhex.stuff;

import dev.kineticcat.complexhex.api.casting.iota.QuaternionIota;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;

import static at.petrak.hexcasting.api.utils.HexUtils.fixNAN;

public class Quaternion extends Quaterniond {

    // Конструктор для всех 4 компонентов
    public Quaternion(double w, double x, double y, double z) {
        super(x, y, z, w);
    }

    // Пустой конструктор (по умолчанию w=1, остальное 0)
    public Quaternion() {
        super();
    }

    // Конструктор из оси и угла
    public Quaternion(Vec3 axis, double angle) {
        super();
        double C = Math.cos(angle / 2);
        double S = Math.sin(angle / 2);
        this.w = C;
        this.x = axis.x * S;
        this.y = axis.y * S;
        this.z = axis.z * S;
    }

    public QuaternionIota asIota() {
        return new QuaternionIota(this);
    }

    public Quaternion fixNaN() {
        return new Quaternion(
                fixNAN(this.w),
                fixNAN(this.x),
                fixNAN(this.y),
                fixNAN(this.z)
        );
    }

    public Quaternion Qadd(Quaternion that) {
        // Создаем новый кватернион, копируем текущий и прибавляем 'that'
        return (Quaternion) new Quaternion().set(this).add(that);
    }

    public Quaternion Qadd(Double that) {
        // Прибавляем скаляр только к вещественной части (w), или ко всем?
        // Обычно в модах скаляр прибавляют к w, но если нужно ко всем компонентам:
        return new Quaternion(this.w + that, this.x, this.y, this.z);
    }

    public Quaternion Qsub(Quaternion that) {
        // В JOML нет .sub(), поэтому делаем: this + (-that)
        Quaternion negated = (Quaternion) new Quaternion().set(that).mul(-1.0);
        return (Quaternion) new Quaternion().set(this).add(negated);
    }

    public Quaternion Qsub(Double that) {
        // Вычитаем скаляр из вещественной части (w)
        return new Quaternion(this.w - that, this.x, this.y, this.z);
    }

    public Quaternion Qmul(Double that) {
        return (Quaternion) new Quaternion().set(this).mul(that);
    }

    public Quaternion Qmul(Quaternion that) {
        return (Quaternion) new Quaternion().set(this).mul(that);
    }

    public Quaternion Qdiv(Double that) {
        return (Quaternion) new Quaternion().set(this).mul(1.0 / that);
    }

    public Quaternion Qinvert() {
        return (Quaternion) new Quaternion().set(this).invert();
    }
}