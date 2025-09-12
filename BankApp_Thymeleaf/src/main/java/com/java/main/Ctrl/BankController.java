package com.java.main.Ctrl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.java.main.entities.Account;
import com.java.main.services.AccountService;

@Controller
public class BankController {

	@Autowired
	private AccountService accountService;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = accountService.getByUsername(username);
		model.addAttribute("account", account);
		return "dashboard";
	}

	@GetMapping("/register")
	public String showregister() {
		return "register";
	}

	@PostMapping("/register")
	public String registration(@RequestParam String username, @RequestParam String password, Model model) {
		try {
			accountService.registerAccount(username, password);
			return "redirect:/login";
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
		}
		return "register";
	}

	@GetMapping("/login")
	public String showlogin() {
		return "login";
	}

	@PostMapping("/depositCtrl")
	public String deposit(@RequestParam BigDecimal amount, RedirectAttributes model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = accountService.getByUsername(username);
		accountService.deposit(account, amount);
		model.addFlashAttribute("success", "Amount "+amount+" Deposit Successfully");
		return "redirect:/dashboard";
	}

	@PostMapping("/withdrawCtrl")
	public String withdrawal(@RequestParam BigDecimal amount, Model model,RedirectAttributes rd) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = accountService.getByUsername(username);
		try {
			accountService.withdraw(account, amount);
			rd.addFlashAttribute("success", "Amount "+amount+" Withdraw Successfully");
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			return "dashboard";
		}
		return "redirect:/dashboard";
	}

	@GetMapping("/transactions")
	public String transactionHistory( Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = accountService.getByUsername(username);
		model.addAttribute("transactions", accountService.getTransactionHistory(account));
		return "transaction";
	}
	
	@PostMapping("/transfer")
	public String transfer(@RequestParam String toUsername,@RequestParam BigDecimal amount, Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account fromaccount = accountService.getByUsername(username);
		
		try {
			accountService.fundTransfer(fromaccount, toUsername, amount);
		}catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("account", fromaccount);
			return "dashboard";
		}

		return "redirect:/dashboard";
	}

}
