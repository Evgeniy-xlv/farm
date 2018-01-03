/**
 * Copyright (c) 2016-2018 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.pmo.banks;

import com.zerocracy.jstk.Farm;
import com.zerocracy.jstk.Project;
import com.zerocracy.jstk.SoftException;
import com.zerocracy.jstk.cash.Cash;
import com.zerocracy.pm.cost.Ledger;
import com.zerocracy.pmo.People;
import java.io.IOException;

/**
 * Payroll.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.19
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class Payroll {

    /**
     * Farm.
     */
    private final Farm farm;

    /**
     * Ctor.
     * @param frm The farm
     */
    public Payroll(final Farm frm) {
        this.farm = frm;
    }

    /**
     * Pay to someone.
     * @param project The project
     * @param login The login to pay
     * @param amount The amount to pay
     * @param reason The reason
     * @return Payment receipt (short summary of the payment)
     * @throws IOException If fails
     * @checkstyle ParameterNumberCheck (6 lines)
     */
    public String pay(final Project project,
        final String login, final Cash amount,
        final String reason) throws IOException {
        final String wallet = new People(this.farm).wallet(login);
        if (wallet.isEmpty()) {
            throw new SoftException(
                String.format(
                    "@%s doesn't have payment method configured, can't pay.",
                    login
                )
            );
        }
        final String[] parts = wallet.split(":", 2);
        final Bank bank;
        if ("paypal".equals(parts[0])) {
            bank = new Paypal(this.farm);
        } else {
            throw new SoftException(
                String.format(
                    "@%s has an unsupported payment method \"%s\".",
                    login, parts[0]
                )
            );
        }
        final Cash commission = bank.fee(amount);
        final String pid = bank.pay(parts[1], amount, reason);
        new Ledger(project).bootstrap().add(
            new Ledger.Transaction(
                amount.add(commission),
                "liabilities", parts[0],
                "assets", "cash",
                String.format(
                    "%s (amount:%s, commission:%s, PID:%s)",
                    reason, amount, commission, pid
                )
            ),
            new Ledger.Transaction(
                commission,
                "expenses", "jobs",
                "liabilities", parts[0],
                String.format("%s (commission)", reason)
            ),
            new Ledger.Transaction(
                amount,
                "expenses", "jobs",
                "liabilities", String.format("@%s", login),
                reason
            )
        );
        return pid;
    }

}
