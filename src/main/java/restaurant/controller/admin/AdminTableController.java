package restaurant.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restaurant.entity.Area;
import restaurant.entity.TableEntity;
import restaurant.enums.TableStatus;
import restaurant.service.AreaService;
import restaurant.service.TableService;

import java.util.List;

@Controller
@RequestMapping("/admin/table")
@RequiredArgsConstructor
public class AdminTableController {

    private final TableService tableService;
    private final AreaService areaService;

    @GetMapping
    public String viewTableManagement(
            @RequestParam(name = "areaId", required = false) Integer areaId,
            HttpSession session, Model model) {

        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        if (branchId == null) return "redirect:/admin/home";

        List<Area> areas = areaService.getAllByRestaurant(branchId);
        List<TableEntity> tables = (areaId != null)
                ? tableService.getTablesByArea(areaId, branchId)
                : tableService.getTablesByBranch(branchId);

        model.addAttribute("totalCount", tables.size());
        model.addAttribute("freeCount", tables.stream().filter(t -> t.getStatus() == TableStatus.AVAILABLE).count());
        model.addAttribute("servingCount", tables.stream().filter(t -> t.getStatus() == TableStatus.OCCUPIED).count());
        model.addAttribute("reservedCount", tables.stream().filter(t -> t.getStatus() == TableStatus.RESERVED).count());

        model.addAttribute("areas", areas);
        model.addAttribute("tables", tables);
        model.addAttribute("selectedAreaId", areaId);
        model.addAttribute("activePage", "table");

        return "admin/admin-table";
    }

    @PostMapping("/area/create")
    public String createArea(@ModelAttribute Area area, HttpSession session, RedirectAttributes ra) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            areaService.saveArea(area, branchId);
            ra.addFlashAttribute("success", "Thêm khu vực thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/table";
    }

    @PostMapping("/area/delete/{id}")
    public String deleteArea(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            areaService.deleteArea(id, branchId);
            ra.addFlashAttribute("success", "Xóa khu vực thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa khu vực đang có bàn!");
        }
        return "redirect:/admin/table";
    }

    @PostMapping("/create")
    public String createTable(@RequestParam Integer areaId,
                              @ModelAttribute TableEntity table,
                              HttpSession session, RedirectAttributes ra) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            tableService.createTable(areaId, table, branchId);
            ra.addFlashAttribute("success", "Thêm bàn mới thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/table" + (areaId != null ? "?areaId=" + areaId : "");
    }

    @PostMapping("/update-status/{id}")
    public String updateTableStatus(@PathVariable Long id,
                                    @RequestParam TableStatus status,
                                    HttpSession session, RedirectAttributes ra) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            tableService.updateStatus(id, status, branchId);
            ra.addFlashAttribute("success", "Cập nhật trạng thái bàn thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi cập nhật!");
        }
        return "redirect:/admin/table";
    }

    @PostMapping("/delete/{id}")
    public String deleteTable(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        Integer branchId = (Integer) session.getAttribute("currentBranchId");
        try {
            tableService.deleteTable(id, branchId);
            ra.addFlashAttribute("success", "Đã xóa bàn!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi xóa bàn!");
        }
        return "redirect:/admin/table";
    }
}